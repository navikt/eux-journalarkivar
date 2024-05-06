package no.nav.eux.journalarkivar.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journalarkivar.integration.eux.journal.client.EuxJournalClient
import no.nav.eux.journalarkivar.integration.eux.navrinasak.client.EuxNavRinasakClient
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus
import no.nav.eux.journalarkivar.integration.external.saf.client.SafClient
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalpost
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalposttype.U
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
            .mapNotNull { it.dokumentInfoId }
            .also { log.info { "${it.size} har dokumentInfoId" } }
            .mapNotNull { it.journalpost }
            .also { log.info { "${it.size} har tilknyttet journalpost" } }
            .filter { it.journalposttype == U }
            .also { log.info { "${it.size} er utgående og blir forsøkt feilregistrert" } }
            .forEach { it.feilregistrer() }
    }

    val EuxSedJournalstatus.dokumentInfoId
        get() =
            try {
                euxNavRinasakClient.finn(rinasakId)
                    .dokumenter
                    ?.single { it erSammeSedSom this }
                    ?.dokumentInfoId!!
            } catch (e: RuntimeException) {
                log.error(e) { "Fant ikke dokumentInfoId" }
                null
            }

    val String.journalpost
        get() =
            try {
                mdc(journalpostId = this)
                safClient.firstTilknyttetJournalpostOrNull(this)
            } catch (e: RuntimeException) {
                log.error(e) { "Fant ikke journalpost" }
                null
            }

    fun SafJournalpost.feilregistrer() {
        mdc(journalpostId = journalpostId)
        try {
            euxJournalClient settStatusAvbrytFor journalpostId
            log.info { "Journalpost feilregistrert" }
        } catch (e: RuntimeException) {
            log.error(e) { "Feilregistrering feilet" }
        }
    }
}
