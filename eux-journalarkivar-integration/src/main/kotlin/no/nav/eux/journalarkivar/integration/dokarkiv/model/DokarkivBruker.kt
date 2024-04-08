package no.nav.eux.journalarkivar.integration.dokarkiv.model

data class DokarkivBruker(
    val id: String,
    val idType: DokarkivBrukerType
)

enum class DokarkivBrukerType {
    FNR, ORGNR, AKTOERID
}
