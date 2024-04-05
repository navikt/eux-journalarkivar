package no.nav.eux.journalarkivar.integration.euxnavrinasak.model

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
}
