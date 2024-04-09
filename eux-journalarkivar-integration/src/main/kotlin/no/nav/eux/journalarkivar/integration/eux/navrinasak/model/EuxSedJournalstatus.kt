package no.nav.eux.journalarkivar.integration.eux.navrinasak.model

import java.time.OffsetDateTime
import java.util.*

data class EuxSedJournalstatus(
    val rinasakId: Int,
    val sedId: UUID,
    val sedVersjon: Int,
    val sedJournalstatus: Status,
    val endretBruker: String,
    val endretTidspunkt: OffsetDateTime,
    val opprettetBruker: String,
    val opprettetTidspunkt: OffsetDateTime
) {
    enum class Status {
        JOURNALFOERT, UKJENT
    }

    val put
        get() =
            EuxSedJournalstatusPut(
                rinasakId = rinasakId,
                sedId = sedId,
                sedVersjon = sedVersjon,
                sedJournalstatus = sedJournalstatus
            )
}
