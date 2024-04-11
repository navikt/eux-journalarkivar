package no.nav.eux.journalarkivar.webapp.mock

import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun safResponse(body: String) =
    when {
        body.contains("454221906") -> document("454221906")
        body.contains("454221907") -> document("454221907")
        body.contains("454221908") -> document("454221908")
        else -> throw RuntimeException("No SAF response defined for $body}")
    }

fun document(dokumentInfoId: String) =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(safResponseBody(dokumentInfoId))
    }

fun safResponseBody(dokumentInfoId: String) =
    Any::class::class.java
        .getResource("/dataset/saf/get-response-body-$dokumentInfoId.json")!!
        .readText()