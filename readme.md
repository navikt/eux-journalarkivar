### EUX Journalarkivar

Journalarkivar for EUX

## Brukte teknologier
* Kotlin
* Spring
* Maven

#### Avhengigheter

* JDK 21

#### Kjøring av tester lokalt

Kjøring av database-tester krever en kjørende PostgreSQL database med følgende variabler satt korrekt:

```
set -x DATABASE_HOST localhost
set -x DATABASE_USERNAME postgres
set -x DATABASE_DATABASE postgres
set -x DATABASE_PORT 5432
```
