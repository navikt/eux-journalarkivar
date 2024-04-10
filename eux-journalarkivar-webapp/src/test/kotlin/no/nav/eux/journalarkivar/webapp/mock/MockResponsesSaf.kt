package no.nav.eux.journalarkivar.webapp.mock

import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun safResponse(body: String) =
    when {
        body.contains("454221906") -> document454221906()
        body.contains("454221907") -> document454221907()
        else -> throw RuntimeException("No SAF response defined for $body}")
    }

fun document454221906() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(safResponseBody454221906)
    }

fun document454221907() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(safResponseBody454221907)
    }

val safResponseBody454221906 =
    Any::class::class.java
        .getResource("/dataset/saf/get-response-body-454221906.json")!!
        .readText()

val safResponseBody454221907 =
    Any::class::class.java
        .getResource("/dataset/saf/get-response-body-454221907.json")!!
        .readText()
