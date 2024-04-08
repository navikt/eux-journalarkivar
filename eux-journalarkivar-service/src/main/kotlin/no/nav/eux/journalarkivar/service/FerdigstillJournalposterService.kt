package no.nav.eux.journalarkivar.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journalarkivar.integration.euxnavrinasak.client.EuxNavRinasakClient
import no.nav.eux.journalarkivar.integration.euxnavrinasak.model.EuxSedJournalstatus
import no.nav.eux.journalarkivar.integration.euxrinaapi.client.EuxRinaApiClient
import no.nav.eux.journalarkivar.integration.saf.client.SafClient
import org.springframework.stereotype.Service

@Service
class FerdigstillJournalposterService(
    val euxNavRinasakClient: EuxNavRinasakClient,
    val euxRinaApiClient: EuxRinaApiClient,
    val safClient: SafClient,
) {

    val log = logger {}

    fun ferdigstillJournalposter() {
        val euxSedJournalstatuser = euxNavRinasakClient.finn()
        log.info { "${euxSedJournalstatuser.size} dokumenter har ukjent journalføringsstatus" }
        euxSedJournalstatuser.forEach { tryFerdigstillJournalpost(it) }
        log.info { "ferdigstilling av journalposter gjennomført" }

    }

    fun tryFerdigstillJournalpost(journalstatus: EuxSedJournalstatus) =
        try {
            ferdigstillJournalpost(journalstatus)
        } catch (e: Exception) {
            log.error(e) { "Feil ved ferdigstilling av journalpost" }
        }

    fun ferdigstillJournalpost(journalstatus: EuxSedJournalstatus) {
        log.info { "henter rinasak ${journalstatus.rinasakId}" }
        val rinasak = euxRinaApiClient.euxRinaSakOversikt(journalstatus.rinasakId)
        safClient.dokumentoversiktBrukerStringTest(rinasak.fnr!!)
    }
}
