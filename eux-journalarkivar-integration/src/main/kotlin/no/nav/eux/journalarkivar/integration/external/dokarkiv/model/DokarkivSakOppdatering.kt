package no.nav.eux.journalarkivar.integration.external.dokarkiv.model

data class DokarkivSakOppdatering(
    val sakstype: String,
    val fagsaksystem: String?,
    val fagsakId: String?,
)
