package no.nav.eux.journalarkivar.integration.external.saf.model

data class SafDokument(
    val dokumentInfoId: String,
    val tittel: String,
    val brevkode: String?
)
