package no.nav.eux.journalarkivar.service

import org.slf4j.MDC
import java.util.*

fun <T> T.mdc(
    rinasakId: Int? = null,
    dokumentInfoId: String? = null,
    sedId: UUID? = null,
    sedVersjon: Int? = null,
    journalpostId: String? = null
): T {
    "rinasakId" leggTil rinasakId
    "dokumentInfoId" leggTil dokumentInfoId
    "sedId" leggTil sedId
    "sedVersjon" leggTil sedVersjon
    "journalpostId" leggTil journalpostId
    return this
}

private infix fun String.leggTil(value: Any?) {
    if (value != null) MDC.put(this, "$value")
}
