package no.nav.eux.journalarkivar.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journalarkivar.integration.eux.journal.client.EuxJournalClient
import no.nav.eux.journalarkivar.integration.eux.navrinasak.client.EuxNavRinasakClient
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.Dokument
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxNavRinasak
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus.Status.*
import no.nav.eux.journalarkivar.integration.eux.oppgave.client.EuxOppgaveClient
import no.nav.eux.journalarkivar.integration.eux.rinaapi.client.EuxRinaApiClient
import no.nav.eux.journalarkivar.integration.external.dokarkiv.client.DokarkivClient
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivBruker
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivBrukerType
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivJournalpostOppdatering
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivSakOppdatering
import no.nav.eux.journalarkivar.integration.external.saf.client.SafClient
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalpost
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalposttype.I
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalstatus
import no.nav.eux.journalarkivar.integration.external.saf.model.SafSak
import no.nav.eux.journalarkivar.model.SakUtenFerdigstilteJournalposterException
import no.nav.eux.logging.clearLocalMdc
import no.nav.eux.logging.mdc
import no.nav.eux.logging.setAndClearLocalMdc
import org.springframework.stereotype.Service

@Service
class FerdigstillJournalposterService(
    val euxNavRinasakClient: EuxNavRinasakClient,
    val euxJournalClient: EuxJournalClient,
    val safClient: SafClient,
    val dokarkivClient: DokarkivClient,
    val euxOppgaveClient: EuxOppgaveClient,
    val euxRinaApiClient: EuxRinaApiClient,
) {

    val log = logger {}

    fun ferdigstillJournalposter() {
        euxNavRinasakClient
            .sedJournalstatuser(UKJENT)
            .also { log.info { "${it.size} dokumenter har status ukjent" } }
            .forEach { it.tryFerdigstillJournalpost() }
        clearLocalMdc()
        euxNavRinasakClient
            .sedJournalstatuser(FEILREGISTRERT)
            .also { log.info { "${it.size} dokumenter har status feilregistrert" } }
            .forEach { it.tryFerdigstillJournalpost() }
        clearLocalMdc()
        log.info { "Ferdigstilling av journalposter utført" }
    }

    fun EuxSedJournalstatus.tryFerdigstillJournalpost() =
        try {
            setAndClearLocalMdc(rinasakId = rinasakId, sedId = sedId, sedVersjon = sedVersjon)
            ferdigstillJournalpost()
        } catch (e: SakUtenFerdigstilteJournalposterException) {
            log.debug { e.message }
        } catch (e: Exception) {
            log.error(e) { "Feil ved ferdigstilling av journalpost: ${e.message}" }
        }

    fun EuxSedJournalstatus.ferdigstillJournalpost() {
        val navRinasak = euxNavRinasakClient.finn(rinasakId)
        val dokument = navRinasak
            .dokumenter
            ?.firstOrNull { it erSammeSedSom this }
            ?: throw RuntimeException("Fant ikke dokument i nav rinasak")
        mdc(sedType = dokument.sedType, dokumentInfoId = dokument.dokumentInfoId)
        ferdigstillJournalpost(navRinasak, dokument.dokumentInfoId)
        this settStatusTil JOURNALFOERT
    }

    fun EuxSedJournalstatus.ferdigstillJournalpost(
        navRinasak: EuxNavRinasak,
        dokumentInfoId: String
    ) {
        val journalpost = safClient
            .firstTilknyttetJournalpostOrNull(dokumentInfoId)
            ?: throw RuntimeException("Fant ikke journalpost for dokumentInfoId")
        mdc(journalpostId = journalpost.journalpostId)
        log.debug { "Journalpost har status ${journalpost.journalstatus}" }
        when {
            journalpost.journalstatus.erJournalfoert -> log.info { "Journalpost er allerede journalført" }
            journalpost.journalstatus == SafJournalstatus.FEILREGISTRERT -> log.info { "Journalpost er feilregistrert"}
            else -> journalpost.ferdigstillJournalpost(navRinasak)
        }
    }

    infix fun EuxSedJournalstatus.settStatusTil(journalstatus: EuxSedJournalstatus.Status) {
        euxNavRinasakClient.put(copy(sedJournalstatus = journalstatus).put)
        log.info { "Sed journalstatus satt til $journalstatus" }
    }

    fun SafJournalpost.ferdigstillJournalpost(
        navRinasak: EuxNavRinasak,
    ) {
        if (avsenderMottaker?.navn.isNullOrBlank())
            oppdaterAvsenderMottakerNavn(navRinasak)
        val ferdigstiltJournalpost = navRinasak.firstFerdigstiltJournalpostOrNull()
        when {
            ferdigstiltJournalpost != null -> this ferdigstillMed ferdigstiltJournalpost
            else -> throw SakUtenFerdigstilteJournalposterException()
        }
    }

    fun EuxNavRinasak.firstFerdigstiltJournalpostOrNull() =
        dokumenter!!
            .mapNotNull { safClient.firstTilknyttetJournalpostOrNull(it.dokumentInfoId) }
            .firstOrNull { it.journalstatus.erJournalfoert }

    infix fun SafJournalpost.ferdigstillMed(ferdigstiltJournalpost: SafJournalpost) {
        this oppdaterMed ferdigstiltJournalpost
        euxJournalClient.ferdigstill(journalpostId)
        log.info { "Ferdigstilling av journalpost utført" }
        when (journalposttype) {
            I -> {
                ferdigstillTilknyttedeOppgaver(journalpostId, ferdigstiltJournalpost.bruker!!.id)
                lagBehandleSedOppgave(journalpostId)
            }
            else -> log.info { "Journalposttype $journalposttype, lager ikke behandle sed oppgave" }
        }
    }

    infix fun SafJournalpost.oppdaterMed(eksisterendeJournalpost: SafJournalpost) {
        log.info { "Oppdaterer $journalpostId med utgangspunkt i ${eksisterendeJournalpost.journalpostId}" }
        val eksisterendeSak = eksisterendeJournalpost.sak!!
        val eksisterendeBruker = eksisterendeJournalpost.bruker!!
        val dokarkivBruker = DokarkivBruker(
            id = eksisterendeBruker.id,
            idType = DokarkivBrukerType.valueOf(eksisterendeBruker.type)
        )
        val dokarkivSak =
            when {
                eksisterendeSak.sakstype == "GENERELL_SAK" -> dokarkivSakOppdateringGenerell
                else -> eksisterendeSak.toDokarkivSakOppdateringFagsak()
            }
        val dokarkivOppdatering = DokarkivJournalpostOppdatering(
            sak = dokarkivSak,
            bruker = dokarkivBruker,
            tema = eksisterendeSak.tema!!
        )
        dokarkivClient.oppdater(journalpostId, dokarkivOppdatering)
        log.info { "Journalpost oppdatert" }
    }

    fun ferdigstillTilknyttedeOppgaver(journalpostId: String, personident: String) {
        euxOppgaveClient.ferdigstillOppgave(
            journalpostId = journalpostId,
            personident = personident
        )
        log.info { "Ferdigstilling av tilhørende oppgaver utført" }
    }

    fun lagBehandleSedOppgave(journalpostId: String) {
        euxOppgaveClient.behandleSed(journalpostId)
        log.info { "Behandle sed oppgave opprettet" }
    }

    fun SafJournalpost.oppdaterAvsenderMottakerNavn(navRinasak: EuxNavRinasak) {
        val rinasak = euxRinaApiClient.euxRinaSakOversikt(navRinasak.rinasakId)
        val avsenderMottakerNavn = if (journalposttype == I)
            rinasak.motpartFormatertNavn
        else
            rinasak.navInstitusjonNavn
        dokarkivClient.oppdater(journalpostId, avsenderMottakerNavn)
        log.info { "Oppdaterte avsender mottaker i dokarkiv" }
    }
}

val dokarkivSakOppdateringGenerell =
    DokarkivSakOppdatering(
        sakstype = "GENERELL_SAK",
        fagsaksystem = null,
        fagsakId = null
    )

fun SafSak.toDokarkivSakOppdateringFagsak() =
    DokarkivSakOppdatering(
        sakstype = sakstype!!,
        fagsaksystem = fagsaksystem,
        fagsakId = fagsakId
    )

infix fun Dokument.erSammeSedSom(
    journalstatus: EuxSedJournalstatus
) = sedId == journalstatus.sedId && sedVersjon == journalstatus.sedVersjon
