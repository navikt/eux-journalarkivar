package no.nav.eux.journalarkivar.integration.dokarkiv.model

data class DokarkivSakOppdatering(
    val sakstype: String,
    val fagsaksystem: String?,
    val fagsakId: String?,
)
