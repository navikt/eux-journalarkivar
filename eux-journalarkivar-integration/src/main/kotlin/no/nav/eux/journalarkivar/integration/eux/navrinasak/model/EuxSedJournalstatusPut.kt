package no.nav.eux.journalarkivar.integration.eux.navrinasak.model

import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus.Status
import java.util.*

data class EuxSedJournalstatusPut(
    val rinasakId: Int,
    val sedId: UUID,
    val sedVersjon: Int,
    val sedJournalstatus: Status,
)
