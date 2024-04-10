package no.nav.eux.journalarkivar.webapp.mock

import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun getEuxNavRinasakResponse() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(getNavRinasakResponseJson)
    }

fun postSedJournalstatuserFinnResponse() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(postSedJournalstatuserFinnResponseBody)
    }

val getNavRinasakResponseJson =
    Any::class::class.java
        .getResource("/dataset/eux-nav-rinasak/get-response-body.json")!!
        .readText()

val postSedJournalstatuserFinnResponseBody =
    Any::class::class.java
        .getResource("/dataset/eux-nav-rinasak/post-sed-journalstatuser-finn-response-body.json")!!
        .readText()
