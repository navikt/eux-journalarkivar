package no.nav.eux.journalarkivar.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journalarkivar.integration.eux.journal.client.EuxJournalClient
import no.nav.eux.journalarkivar.integration.eux.navrinasak.client.EuxNavRinasakClient
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.Dokument
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus.Status.*
import no.nav.eux.journalarkivar.integration.external.saf.client.SafClient
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalpost
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalposttype.U
import no.nav.eux.logging.clearLocalMdc
import no.nav.eux.logging.mdc
import org.springframework.stereotype.Service
import java.time.OffsetDateTime.now

@Service
class FeilregistrerJournalposterService(
    val euxNavRinasakClient: EuxNavRinasakClient,
    val euxJournalClient: EuxJournalClient,
    val safClient: SafClient,
) {

    val log = logger {}

    fun feilregistrerJournalposter() {
        euxNavRinasakClient
            .sedJournalstatuser(FEILET_FEILREGISTRER)
            .also { log.info { "${it.size} dokumenter med feilet feilregistrering, forsøker på nytt" } }
            .forEach { it.tryFeilregistrerJournalpost() }
        clearLocalMdc()
        euxNavRinasakClient
            .sedJournalstatuser(UKJENT)
            .also { log.info { "${it.size} kandidater for feilregistrering" } }
            .filter { it.opprettetTidspunkt.isBefore(now().minusDays(30)) }
            .also { log.info { "${it.size} er mer enn 30 dager gamle" } }
            .forEach { it.tryFeilregistrerJournalpost() }
        clearLocalMdc()
    }

    fun EuxSedJournalstatus.tryFeilregistrerJournalpost() {
        try {
            val dokument = dokument()
            mdc(
                rinasakId = rinasakId,
                sedId = sedId,
                sedVersjon = sedVersjon,
                sedType = dokument.sedType,
                dokumentInfoId = dokument.dokumentInfoId,
            )
            val journalpost = journalpost(dokument.dokumentInfoId)
            mdc(journalpostId = journalpost.journalpostId)
            if (journalpost.bruker == null && journalpost.journalposttype == U) {
                feilregistrer(dokument, journalpost)
            }
        } catch (e: Exception) {
            when (sedJournalstatus) {
                FEILET_FEILREGISTRER -> {
                    log.error { "Feilregistrering feilet for andre gang, gir opp: ${e.message}" }
                    settStatusTil(KORRUPT, feilmelding = e.message)
                }
                else -> {
                    log.warn { "Feilregistrering feilet, forsøker igjen neste kjøring: ${e.message}" }
                    settStatusTil(FEILET_FEILREGISTRER, feilmelding = e.message)
                }
            }
        }
    }

    fun EuxSedJournalstatus.feilregistrer(
        dokument: Dokument,
        journalpost: SafJournalpost
    ) {
        mdc(
            rinasakId = rinasakId,
            sedId = sedId,
            sedVersjon = sedVersjon,
            sedType = dokument.sedType,
            dokumentInfoId = dokument.dokumentInfoId,
            journalpostId = journalpost.journalpostId
        )
        euxJournalClient settStatusAvbrytFor journalpost.journalpostId
        log.info { "Journalpost feilregistrert" }
        settStatusTil(FEILREGISTRERT)
    }

    fun EuxSedJournalstatus.settStatusTil(
        journalstatus: EuxSedJournalstatus.Status,
        feilmelding: String? = null
    ) {
        euxNavRinasakClient.put(
            copy(sedJournalstatus = journalstatus).put.copy(feilmelding = feilmelding)
        )
        log.info { "Journalstatus satt til $journalstatus" }
    }

    fun EuxSedJournalstatus.dokument() =
        try {
            mdc(
                rinasakId = rinasakId,
                sedId = sedId,
                sedVersjon = sedVersjon,
            )
            euxNavRinasakClient.finn(rinasakId)
                .dokumenter!!
                .single { it erSammeSedSom this }
        } catch (e: RuntimeException) {
            log.error(e) { "Fant ikke dokument i nav rinasak" }
            throw e
        }

    fun journalpost(dokumentInfoId: String) =
        try {
            safClient.firstTilknyttetJournalpostOrNull(dokumentInfoId)!!
        } catch (e: RuntimeException) {
            log.error(e) { "Fant ikke journalpost for dokumentInfoId: $dokumentInfoId" }
            throw e
        }

    data class DokumentForFeilregistrering(
        val euxSedJournalstatus: EuxSedJournalstatus,
        val journalpost: SafJournalpost,
        val dokument: Dokument,
    )
}
