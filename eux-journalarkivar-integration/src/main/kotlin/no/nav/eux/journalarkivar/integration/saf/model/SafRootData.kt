package no.nav.eux.journalarkivar.integration.saf.model

data class SafJournalpostRoot(val data: SafJournalpostData)
data class SafJournalpostData(val journalpost: SafJournalpost)
data class SafSakerRoot(val data: SafSakerData)
data class SafSakerData(val saker: List<SafSak>)
data class SafTilknyttedeJournalposterRoot(val data: SafTilknyttedeJournalposterData)
data class SafTilknyttedeJournalposterData(val tilknyttedeJournalposter: List<SafJournalpost>)
data class SafDokumentoversiktBrukerRoot(val data: SafDokumentoversiktBrukerData)
data class SafDokumentoversiktBrukerData(val dokumentoversiktBruker: List<SafJournalpost>)
