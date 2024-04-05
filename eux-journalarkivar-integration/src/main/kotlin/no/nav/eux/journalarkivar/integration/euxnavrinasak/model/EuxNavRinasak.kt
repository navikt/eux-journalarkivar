package no.nav.eux.journalarkivar.integration.euxnavrinasak.model

import java.time.OffsetDateTime

data class EuxNavRinasak(
    val rinasakId: Int,
    val opprettetBruker: String,
    val opprettetTidspunkt: OffsetDateTime,
    val overstyrtEnhetsnummer: String? = null,
    val initiellFagsak: Fagsak? = null,
    val dokumenter: List<Dokument>? = null
)
