package no.nav.eux.journalarkivar.integration.external.saf.model

data class SafRoot<T>(
    val data: T?,
    val errors: List<SafError>?,
)
data class SafJournalpostData(val journalpost: SafJournalpost)
data class SafSakerData(val saker: List<SafSak>)
data class SafTilknyttedeJournalposterData(val tilknyttedeJournalposter: List<SafJournalpost>)
data class SafDokumentoversiktBrukerData(val dokumentoversiktBruker: SafDokumentoversiktBruker)
data class SafDokumentoversiktBruker(val journalposter: List<SafJournalpost>)

data class SafError (
    val message: String?,
)
