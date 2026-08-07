# Runbook: Dagens dependency-/CI-/Docker-oppgaver (gjenbrukbar på tvers av eux-repoer)

> Kopier denne fila inn i en ny agent-sesjon som startes **i mål-repoet** (f.eks. `eux-person-oppdatering`).
> Kjør oppgavene i rekkefølge. Alt er skrevet repo-agnostisk — verifiser de repo-spesifikke antakelsene i "Discovery" først.
>
> **Viktig miljøkonvensjon:** Alle Maven-kommandoer kjøres **alltid** med `-s .github/settings.xml`
> (settings.xml definerer navikt GitHub Packages-repoene og server-credentials).
> `settings.xml` leser tokens fra env:
> - `READER_TOKEN` → brukes for `eux-parent-pom` og `eux-versions-maven-plugin` (username `x-access-token`).
> - `GITHUB_ACTOR` + `GITHUB_TOKEN` → brukes for `github-package-registry-navikt`.
>
> Sett disse før du kjører Maven. Har du `gh` innlogget, funker:
> `T=$(gh auth token); export READER_TOKEN="$T" GITHUB_TOKEN="$T" GITHUB_ACTOR="x-access-token"`.
>
> Prosjektene targeter **Java 25**. Hvis maskinen din allerede har JDK 25 som global standard,
> trengs ingen ekstra `JAVA_HOME`-setting. Defaulter maskinen til en *nyere* JDK (f.eks. 26),
> sett `JAVA_HOME` til en JDK 25, f.eks.
> `export JAVA_HOME=$(/usr/libexec/java_home -v 25)` (macOS).

---

## Discovery (kjør først — tilpasser planen til repoet)

```bash
# Fra repo-roten:
grep -nE "parent-pom|<version>|\.version>" pom.xml | head -40
grep -niE "jackson|lz4|tomcat|spring-kafka|postgresql|netty" pom.xml
ls .github/settings.xml
cat Dockerfile 2>/dev/null
ls .dockerignore 2>/dev/null && echo "dockerignore finnes" || echo "mangler .dockerignore"
cat .github/workflows/build.yaml 2>/dev/null
```

Noter:
- **Nåværende parent-pom-versjon** (skal opp til `2.0.20`).
- **Jar-navn** = `<finalName>` / artifactId (brukes i Dockerfile: `target/<artifactId>.jar`). Kall det `<ARTIFACT>` under.
- **Bruker repoet Kafka?** (`spring-kafka` i pom) → avgjør om lz4-stegene er relevante.
- **Finnes lokal `tomcat.version`-property?** → bump den; hvis den ikke finnes, ikke legg til (sjekk om parent allerede gir ønsket Tomcat).
- **Finnes lokal `tomcat.version`-property?** → bump den; hvis den ikke finnes, ikke legg til (sjekk om parent allerede gir ønsket Tomcat). Verifiser *alltid* effektiv versjon med `dependency:tree` — property alene kan være «død» ved BOM import-scope (se Oppgave 1).
- **Finnes utdaterte `org.lz4:lz4-java`-eksklusjoner?** → fjernes.
- **Postgresql-driverversjon** (`org.postgresql:postgresql` i `dependency:tree`) → skal opp til `42.7.13` (se Oppgave 5).
- **Netty-versjon** (`io.netty:netty-*` i `dependency:tree`) → skal opp til `4.2.16.Final` (se Oppgave 6). Netty er som regel transitivt (f.eks. via reactor-netty/grpc) og BOM-styrt — samme import-scope-mekanikk som Tomcat/postgresql.
- **Har `build.yaml` allerede en `permissions`-blokk?** → hvis den er bred write og jobben kun bygger/tester, skal den erstattes med read-only (se Oppgave 2).

---

## Oppgave 1 — Bump parent-pom til 2.0.20 (+ Tomcat, + lz4)  → egen grein + PR

**Grein:** `bump/parent-pom-2.0.20` (fra oppdatert `main`).

### Endringer i `pom.xml`
1. **parent-pom** `<version>…</version>` → **`2.0.20`**.
   - Parent 2.0.20 leverer automatisk (verifisert): Jackson 3 (`tools.jackson`) = **3.2.1**,
     Jackson 2 (`com.fasterxml.jackson`) = **2.22.1**, `jackson-annotations` = **2.22**.
   - Ingen lokale Jackson-properties trengs (arves via parent-BOM).
2. **Tomcat** (kun hvis lokal property finnes): `<tomcat.version>` → **`11.0.24`**.
   Parent styrer *ikke* Tomcat, så property beholdes.
   - ⚠️ **Kjent felle (verifisert i eux-person-oppdatering):** Hvis parent-pom importerer Spring
     Boot-BOM med **import-scope**, slår `<tomcat.version>`-property *alene ikke* gjennom på den
     administrerte tomcat-versjonen — jaren blir liggende på BOM-ens verdi (f.eks. 11.0.22) selv om
     `mvn help:evaluate -Dexpression=tomcat.version` viser 11.0.24. Sjekk med `dependency:tree`.
     Hvis tomcat-embed ikke ble 11.0.24, legg til eksplisitt `dependencyManagement` som pinner alle
     tre artefaktene til `${tomcat.version}`:
     ```xml
     <dependencyManagement>
       <dependencies>
         <dependency>
           <groupId>org.apache.tomcat.embed</groupId>
           <artifactId>tomcat-embed-core</artifactId>
           <version>${tomcat.version}</version>
         </dependency>
         <dependency>
           <groupId>org.apache.tomcat.embed</groupId>
           <artifactId>tomcat-embed-el</artifactId>
           <version>${tomcat.version}</version>
         </dependency>
         <dependency>
           <groupId>org.apache.tomcat.embed</groupId>
           <artifactId>tomcat-embed-websocket</artifactId>
           <version>${tomcat.version}</version>
         </dependency>
       </dependencies>
     </dependencyManagement>
     ```
3. **lz4 (kun hvis repoet bruker spring-kafka):**
   - Fjern de utdaterte `<exclusions>` med `org.lz4:lz4-java` på `spring-kafka` og `spring-kafka-test`
     (kafka-clients bruker nå `at.yawk.lz4` — de gamle eksklusjonene treffer ikke lenger).
   - Legg til eksplisitt pin (overstyrer transitiv 1.10.1):
     ```xml
     <!-- i <properties> -->
     <lz4.version>1.11.1</lz4.version>

     <!-- i <dependencies>, f.eks. rett etter spring-kafka -->
     <dependency>
         <groupId>at.yawk.lz4</groupId>
         <artifactId>lz4-java</artifactId>
         <version>${lz4.version}</version>
         <scope>compile</scope>
     </dependency>
     ```
   - **Merk:** `at.yawk.lz4` skal *ikke* ekskluderes (ingen kjente sårbarheter) — den skal beholdes/bumpes.

### Verifisering
```bash
export JAVA_HOME=<sti-til-jdk-25>
export GITHUB_ACTOR=<din-bruker>   # eller x-access-token i CI
mvn -s .github/settings.xml dependency:tree -DoutputFile=/tmp/dtree.txt
grep -iE "tools\.jackson|com\.fasterxml\.jackson|tomcat-embed|lz4" /tmp/dtree.txt
```
Forvent: jackson3=3.2.1, jackson2 core/databind=2.22.1, annotations=2.22, tomcat-embed=11.0.24,
`at.yawk.lz4:lz4-java:1.11.1`, ingen `org.lz4:lz4-java`.
Ser du fortsatt tomcat-embed 11.0.22 her, se «Kjent felle» over (legg til `dependencyManagement`).

### Build-caveat (kjent sandbox-problem)
`mvn clean install` kan feile lokalt i sandbox pga. to *miljø*-årsaker (ikke koden):
- **Mockito/Byte Buddy self-attach** feiler på nyere JDK/sandbox → workaround:
  `-DargLine="-javaagent:$(find ~/.m2 -name 'mockito-core-*.jar' ! -name '*sources*' | sort | tail -1)"`
- **MockServer-tester** som binder port 1080 blokkeres av sandbox-nettverk.
Verifiser derfor rene enhetstester lokalt; full suite kjøres av CI. Bekreft at samme feil finnes på `main`
(da er det ikke forårsaket av endringen).

### Commit / PR
Conventional commit, push grein, `gh pr create --base main`.
Commit-melding-mal:
`chore(deps): bump parent-pom to 2.0.20, tomcat to 11.0.24, pin lz4-java 1.11.1`

---

## Oppgave 2 — build-workflow permissions  → egen grein + PR

**Grein:** `ci/build-workflow-permissions` (fra `main`).

I `.github/workflows/build.yaml`, legg til top-level (mellom `on:` og `jobs:`):
```yaml
permissions:
  contents: read
  packages: read
```
- `contents: read` for checkout, `packages: read` for å hente fra navikt GitHub Packages maven-registry.
- Retter advarsel om manglende `permissions` (least privilege).
- ⚠️ **Kjent variant (verifisert i eux-person-oppdatering):** Workflowen kan *allerede* ha en
  `permissions`-blokk med bred write (f.eks. `deployments/packages/contents: write`, `id-token: write`).
  Hvis build-jobben kun bygger/tester (`mvn clean install`, ingen deploy/publish-steg), **erstatt**
  den brede blokka med read-only over. Verifiser først at ingen steg i jobben faktisk trenger write
  (deploy skjer typisk i egne workflows som `deploy-*.yaml`).

Commit-mal: `ci: add least-privilege permissions to build workflow`

---

## Oppgave 3 — Dockerfile til Nav JVM-standard  → egen grein + PR

**Grein:** `chore/align-dockerfile` (fra `main`).

Mål-innhold (bytt `<ARTIFACT>` med repoets jar-navn):
```dockerfile
FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
ENV TZ="Europe/Oslo"
ENV JDK_JAVA_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
COPY target/<ARTIFACT>.jar /app.jar
CMD ["-jar", "/app.jar"]
```
Endringer å se etter:
- 🐛 **`JAVA_OPTS` → `JDK_JAVA_OPTIONS`** — med base-imagets `java`-entrypoint blir `JAVA_OPTS`
  aldri lest av JVM-en; `JDK_JAVA_OPTIONS` auto-leses → minneflagg tar faktisk effekt.
- **`ENTRYPOINT ["java", …]` → `CMD ["-jar", "/app.jar"]`** (base-imaget har `java` som entrypoint).
- **Fjern `EXPOSE 8080`** (Nais styrer porter).

### `.dockerignore` (legg til hvis den mangler)
```dockerignore
.git
.github
.idea
*.iml
target/*
!target/<ARTIFACT>.jar
src/test
*.md
docker-compose*.yml
.env*
.dockerignore
Dockerfile
```
Whitelister jar-en Dockerfilen kopierer; `.env*` hindrer at hemmeligheter havner i build-context.

Commit-maler:
`chore: align Dockerfile with Nav JVM standard (JDK_JAVA_OPTIONS + CMD)`
`chore: add .dockerignore`
(Kan ligge i samme grein/PR som Dockerfile-endringen.)

---

## Oppgave 4 — Rydd utdaterte Dependabot-PR-er

For hver åpen Dependabot-PR som er superseded (target-versjon allerede i `main`, ofte CONFLICTING/DIRTY):
```bash
gh pr list --author "app/dependabot" --state open
gh pr view <N> --json mergeStateStatus,title,headRefName
# Bekreft at main allerede har target-versjonen, deretter:
gh pr close <N> --delete-branch --comment "Superseded — main er allerede på denne versjonen."
```
(`@dependabot close`-kommentar er ryddigst, men reagerer ikke alltid raskt; fall tilbake til `gh pr close`.)

⚠️ **Verifiser alltid før lukking (eksempel fra eux-person-oppdatering):** En PR som bumper til en
*nyere* versjon enn `main` er **ikke** superseded og skal stå åpen. Der var #27
(`json-flattener 0.17.1 → 0.18.2`, `mergeStateStatus: CLEAN`) en reell oppgradering — ingen handling.
Lukk kun når target-versjonen allerede finnes i `main`.

---

## Oppgave 5 — Bump postgresql-driver til 42.7.13  → egen grein + PR

**Grein:** `bump/postgresql-42.7.13` (fra `main`).

JDBC-driveren `org.postgresql:postgresql` arves som regel fra parent-BOM (f.eks. 42.7.11) uten
eksplisitt versjon i repoet. Samme import-scope-mekanikk som Tomcat: en `postgresql.version`-property
*alene* overstyrer *ikke* den BOM-administrerte versjonen.

### Endringer i `pom.xml`
1. Legg til property: `<postgresql.version>42.7.13</postgresql.version>`.
2. Sett eksplisitt versjon på driver-dependencyen:
   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <version>${postgresql.version}</version>
   </dependency>
   ```

### Verifisering
```bash
mvn -s .github/settings.xml dependency:tree -DoutputFile=/tmp/dtree.txt
grep -i "org.postgresql:postgresql" /tmp/dtree.txt
```
Forvent: `org.postgresql:postgresql:jar:42.7.13:compile`.

Commit-mal: `chore(deps): bump postgresql driver to 42.7.13`

---

## Oppgave 6 — Bump Netty til 4.2.16.Final  → egen grein + PR

**Grein:** `bump/netty-4.2.16` (fra `main`).

Netty (`io.netty:netty-*`) er som regel **transitivt** (f.eks. via reactor-netty, grpc eller
andre klienter) og arves via parent-/Spring Boot-BOM uten eksplisitt versjon i repoet. Samme
import-scope-mekanikk som Tomcat (Oppgave 1) og postgresql (Oppgave 5): en `netty.version`-property
*alene* overstyrer *ikke* den BOM-administrerte versjonen.

### Discovery (kjør først)
```bash
mvn -s .github/settings.xml dependency:tree -DoutputFile=/tmp/dtree.txt
grep -i "io.netty" /tmp/dtree.txt
```
Noter hvilke `io.netty`-artefakter som faktisk trekkes inn og hvilken versjon som arves i dag.
Bruker ikke repoet Netty transitivt i det hele tatt → ingen handling (hopp over oppgaven).

### Endringer i `pom.xml`
1. Legg til property: `<netty.version>4.2.16.Final</netty.version>`.
2. Importer `netty-bom` i `dependencyManagement` (pinner *alle* `io.netty`-artefakter konsistent
   — tryggere enn å pinne enkeltartefakter én for én):
   ```xml
   <dependencyManagement>
     <dependencies>
       <dependency>
         <groupId>io.netty</groupId>
         <artifactId>netty-bom</artifactId>
         <version>${netty.version}</version>
         <type>pom</type>
         <scope>import</scope>
       </dependency>
     </dependencies>
   </dependencyManagement>
   ```
   - ⚠️ **Import-rekkefølge:** Ligger netty allerede lavere i en annen import-BOM (f.eks. Spring
     Boot-BOM via parent), må `netty-bom`-importen stå *før*/*over* den for å vinne, eller pinnes
     eksplisitt. Verifiser *alltid* med `dependency:tree` — property alene er «død» ved import-scope.

### Verifisering
```bash
mvn -s .github/settings.xml dependency:tree -DoutputFile=/tmp/dtree.txt
grep -i "io.netty" /tmp/dtree.txt
```
Forvent: alle `io.netty:*`-artefakter på `4.2.16.Final`, ingen eldre netty-versjoner igjen.

Commit-mal: `chore(deps): bump netty to 4.2.16.Final`

---

## Sjekkliste per repo

- [ ] `bump/parent-pom-2.0.20` — pom endret, dependency:tree verifisert, PR opprettet
- [ ] `ci/build-workflow-permissions` — permissions lagt til, PR opprettet
- [ ] `chore/align-dockerfile` — Dockerfile + .dockerignore, PR opprettet
- [ ] `bump/postgresql-42.7.13` — driver pinnet til 42.7.13, dependency:tree verifisert, PR opprettet
- [ ] `bump/netty-4.2.16` — netty pinnet til 4.2.16.Final, dependency:tree verifisert, PR opprettet
- [ ] Utdaterte Dependabot-PR-er lukket
- [ ] Alle PR-er lenket/rapportert tilbake

## Repoer å gjøre dette i (fyll ut)
- [x] eux-fagmodul-journalfoering (gjort i dag: PR #212, #213, #214; lukket #196)
- [x] eux-person-oppdatering (PR #28 parent-pom+tomcat+lz4, #29 permissions, #30 Dockerfile, #32 postgresql 42.7.13; #27 json-flattener beholdt åpen — ikke superseded)
- [ ] …
