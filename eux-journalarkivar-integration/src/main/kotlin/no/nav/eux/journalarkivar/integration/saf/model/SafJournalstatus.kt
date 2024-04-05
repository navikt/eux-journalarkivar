package no.nav.eux.journalarkivar.integration.saf.model

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class SafJournalstatus(
    val erJournalfoert: Boolean
) {
    JOURNALFOERT(true),
    MOTTATT(true),
    UNDER_ARBEID(true),
    FEILREGISTRERT(true),
    FERDIGSTILT(true),
    EKSPEDERT(true),
    AVBRUTT(true),
    UTGAAR(true),
    UKJENT_BRUKER(true),
    RESERVERT(true),
    OPPLASTING_DOKUMENT(true),

    @JsonEnumDefaultValue
    UKJENT(false);
}

fun safJournalstatusOf(name: String) = SafJournalstatus.values()
    .firstOrNull { safJournalstatus: SafJournalstatus -> safJournalstatus.name == name }
    ?: SafJournalstatus.UKJENT
