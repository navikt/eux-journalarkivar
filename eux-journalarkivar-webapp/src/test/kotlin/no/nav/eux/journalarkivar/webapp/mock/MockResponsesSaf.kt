package no.nav.eux.journalarkivar.webapp.mock

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun safResponse(body: String) =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(safResponseBody(body.dokumentInfoId))
    }

fun safResponseBody(dokumentInfoId: String) =
    Any::class::class.java
        .getResource("/dataset/saf/get-response-body-$dokumentInfoId.json")!!
        .readText()

private val dokumentInfoIdRegex = Regex("""dokumentInfoId: "(\d+)"""")

val String.dokumentInfoId: String
    get() = dokumentInfoIdRegex
        .find(ObjectMapper().readTree(this).findValue("query").asText())
        ?.groupValues
        ?.get(1)
        ?: throw RuntimeException("Fant ikke dokumentInfoId i saf spørring: $this")
