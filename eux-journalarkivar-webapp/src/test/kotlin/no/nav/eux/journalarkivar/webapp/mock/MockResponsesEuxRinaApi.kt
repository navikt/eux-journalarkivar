package no.nav.eux.journalarkivar.webapp.mock

import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun getRinaApiResponse() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(getEuxRinaApiResponseBody)
    }

val getEuxRinaApiResponseBody =
    Any::class::class.java
        .getResource("/dataset/eux-rina-api/eux-rina-api-rinasak.json")!!
        .readText()
