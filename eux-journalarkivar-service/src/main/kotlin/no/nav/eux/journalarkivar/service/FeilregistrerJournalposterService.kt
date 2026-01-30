package no.nav.eux.journalarkivar.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journalarkivar.integration.eux.journal.client.EuxJournalClient
import no.nav.eux.journalarkivar.integration.eux.navrinasak.client.EuxNavRinasakClient
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.Dokument
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus.Status.FEILREGISTRERT
import no.nav.eux.journalarkivar.integration.external.saf.client.SafClient
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalpost
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalposttype.U
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
            .sedJournalstatuser()
            .also { log.info { "${it.size} kandidater for feilregistrering" } }
            .filter { it.opprettetTidspunkt.isBefore(now().minusDays(30)) }
            .also { log.info { "${it.size} er mer enn 30 dager gamle" } }
            .mapNotNull { it.dokumentForFeilregistrering() }
            .also { log.info { "${it.size} har tilknyttet journalpost" } }
            .filter { it.journalpost.bruker == null }
            .also { log.info { "${it.size} har ikke tilknyttet bruker" } }
            .filter { it.journalpost.journalposttype == U }
            .also { log.info { "${it.size} er utgående og blir forsøkt feilregistrert" } }
            .forEach { it.feilregistrer() }
    }

    fun DokumentForFeilregistrering.feilregistrer() {
        mdc(
            rinasakId = euxSedJournalstatus.rinasakId,
            sedId = euxSedJournalstatus.sedId,
            sedVersjon = euxSedJournalstatus.sedVersjon,
            sedType = dokument.sedType,
            dokumentInfoId = dokument.dokumentInfoId,
            journalpostId = journalpost.journalpostId
        )
        try {
            euxJournalClient settStatusAvbrytFor journalpost.journalpostId
            log.info { "Journalpost feilregistrert" }
            euxSedJournalstatus settStatusTil FEILREGISTRERT
        } catch (e: RuntimeException) {
            log.error(e) { "Feilregistrering feilet" }
        }
    }

    infix fun EuxSedJournalstatus.settStatusTil(journalstatus: EuxSedJournalstatus.Status) {
        euxNavRinasakClient.put(copy(sedJournalstatus = journalstatus).put)
        log.info { "Journalstatus satt til $journalstatus" }
    }

    fun EuxSedJournalstatus.dokumentForFeilregistrering(): DokumentForFeilregistrering? =
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
            DokumentForFeilregistrering(this, journalpost, dokument)
        } catch (e: RuntimeException) {
            null
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
