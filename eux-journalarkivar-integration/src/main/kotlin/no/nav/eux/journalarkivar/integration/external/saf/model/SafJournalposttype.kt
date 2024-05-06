package no.nav.eux.journalarkivar.integration.external.saf.model

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class SafJournalposttype {
    I, U, N,

    @JsonEnumDefaultValue
    UKJENT;

}

fun safJournalposttypeOf(name: String) =
    SafJournalposttype.entries
        .firstOrNull { journalposttype: SafJournalposttype -> journalposttype.name == name }
        ?: SafJournalposttype.UKJENT
