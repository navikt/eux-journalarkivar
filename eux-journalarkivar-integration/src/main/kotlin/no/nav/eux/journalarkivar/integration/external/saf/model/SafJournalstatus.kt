package no.nav.eux.journalarkivar.integration.external.saf.model

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue

enum class SafJournalstatus(
    val erJournalfoert: Boolean
) {
    JOURNALFOERT(true),
    FERDIGSTILT(true),
    MOTTATT(false),
    UNDER_ARBEID(false),
    FEILREGISTRERT(false),
    EKSPEDERT(false),
    AVBRUTT(false),
    UTGAAR(false),
    UKJENT_BRUKER(false),
    RESERVERT(false),
    OPPLASTING_DOKUMENT(false),

    @JsonEnumDefaultValue
    UKJENT(false);
}
