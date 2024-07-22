package no.nav.eux.journalarkivar.service

import org.slf4j.MDC
import java.util.*

fun <T> T.mdc(
    arkivarprosess: String? = null,
    rinasakId: Int? = null,
    dokumentInfoId: String? = null,
    sedId: UUID? = null,
    sedVersjon: Int? = null,
    sedType : String? = null,
    journalpostId: String? = null,
): T {
    "arkivarprosess" leggTil arkivarprosess
    "rinasakId" leggTil rinasakId
    "dokumentInfoId" leggTil dokumentInfoId
    "sedId" leggTil sedId
    "sedVersjon" leggTil sedVersjon
    "sedType" leggTil sedType
    "journalpostId" leggTil journalpostId
    return this
}

fun clearLocalMdc() {
    MDC.remove("rinaSakId")
    MDC.remove("dokumentInfoId")
    MDC.remove("sedId")
    MDC.remove("sedVersjon")
    MDC.remove("sedType")
    MDC.remove("journalpostId")
}

fun <T> T.setAndClearLocalMdc(
    arkivarprosess: String? = null,
    rinasakId: Int? = null,
    dokumentInfoId: String? = null,
    sedId: UUID? = null,
    sedVersjon: Int? = null,
    sedType : String? = null,
    journalpostId: String? = null,
) {
    clearLocalMdc()
    mdc(
        arkivarprosess = arkivarprosess,
        rinasakId = rinasakId,
        dokumentInfoId = dokumentInfoId,
        sedId = sedId,
        sedVersjon = sedVersjon,
        sedType = sedType,
        journalpostId = journalpostId
    )
}

private infix fun String.leggTil(value: Any?) {
    if (value != null) MDC.put(this, "$value")
}
