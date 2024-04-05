package no.nav.eux.journalarkivar.integration.euxnavrinasak.model

import java.util.*

data class Dokument(
    val sedId: UUID,
    val sedVersjon: Int,
    val sedType: String,
    val dokumentInfoId: String
)
