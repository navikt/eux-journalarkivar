package no.nav.eux.journalarkivar.integration.external.saf.model

data class SafJournalpost(
    val journalpostId: String,
    val journalposttype: SafJournalposttype = SafJournalposttype.UKJENT,
    val journalstatus: SafJournalstatus = SafJournalstatus.UKJENT,
    val eksternReferanseId: String? = "",
    val dokumenter: List<SafDokument>,
    val tilleggsopplysninger: List<SafTilleggsopplysninger>,
    val sak: SafSak?,
    val bruker: SafBruker?,
    val avsenderMottaker: SafAvsenderMottaker?
)
