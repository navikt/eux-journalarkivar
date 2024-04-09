package no.nav.eux.journalarkivar.integration.external.saf.model

data class SafSak(
    val fagsakId: String?,
    val sakstype: String? = null,
    val tema: String? = null,
    val fagsaksystem: String? = null,
    val arkivsaksnummer: String? = null,
    val arkivsaksystem: String? = null,
)
