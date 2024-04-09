package no.nav.eux.journalarkivar.integration.external.dokarkiv.model

data class DokarkivBruker(
    val id: String,
    val idType: DokarkivBrukerType
)

enum class DokarkivBrukerType {
    FNR, ORGNR, AKTOERID
}
