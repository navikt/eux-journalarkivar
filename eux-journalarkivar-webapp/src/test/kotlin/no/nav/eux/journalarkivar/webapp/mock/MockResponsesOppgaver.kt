package no.nav.eux.journalarkivar.webapp.mock

import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun oppgaverResponse() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(oppgaverResponse)
    }

fun getOppgaverResponse() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(getOppgaverResponse)
    }

val oppgaverResponse = object {}.javaClass.getResource("/dataset/eux-oppgave/oppgave.json")!!.readText()

val getOppgaverResponse = object {}.javaClass.getResource("/dataset/eux-oppgave/oppgaver.json")!!.readText()

const val getOppgaverUri = "/api/v1/oppgaver" +
        "?journalpostId=1234&statuskategori=AAPEN&oppgavetype=JFR&oppgavetype=FDR"
