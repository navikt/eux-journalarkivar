package no.nav.eux.journalarkivar.integration.external.dokarkiv.model

open class DokarkivOppdatering

data class DokarkivJournalpostOppdatering(
    val tema: String,
    val bruker: DokarkivBruker,
    val sak: DokarkivSakOppdatering
) : DokarkivOppdatering()

data class DokarkivJournalpostAvsenderMottakerOppdatering(
    val avsenderMottaker: AvsenderMottaker,
) : DokarkivOppdatering() {
    data class AvsenderMottaker(
        val navn: String
    )
}
