package no.nav.eux.journalarkivar.integration.saf.model

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class SafJournalposttype {
    I, U, N,

    @JsonEnumDefaultValue
    UKJENT;

}

fun safJournalposttypeOf(name: String) =
    SafJournalposttype.values()
        .firstOrNull { journalposttype: SafJournalposttype -> journalposttype.name == name }
        ?: SafJournalposttype.UKJENT
