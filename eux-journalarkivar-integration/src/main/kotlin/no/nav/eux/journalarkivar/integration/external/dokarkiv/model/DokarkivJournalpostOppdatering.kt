package no.nav.eux.journalarkivar.integration.external.dokarkiv.model

data class DokarkivJournalpostOppdatering(
    val tema: String,
    val bruker: DokarkivBruker,
    val sak: DokarkivSakOppdatering
)

data class DokarkivJournalpostSakOppdatering(
    val tema: String,
    val sak: DokarkivSakOppdatering
)

data class DokarkivJournalpostAvsenderMottakerOppdatering(
    val avsenderMottaker: AvsenderMottaker,
) {
    data class AvsenderMottaker(
        val navn: String
    )
}
