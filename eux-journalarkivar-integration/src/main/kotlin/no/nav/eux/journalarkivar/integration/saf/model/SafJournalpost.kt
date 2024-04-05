package no.nav.eux.journalarkivar.integration.saf.model

import eux.nav.rinasak.datasync.integration.saf.*

data class SafJournalpost(
    val journalpostId: String,
    val journalposttype: SafJournalposttype = SafJournalposttype.UKJENT,
    val journalstatus: SafJournalstatus = SafJournalstatus.UKJENT,
    val eksternReferanseId: String = "",
    val dokumenter: List<SafDokument>,
    val tilleggsopplysninger: List<SafTilleggsopplysninger>,
    val sak: SafSak?,
    val bruker: SafBruker?,
)
