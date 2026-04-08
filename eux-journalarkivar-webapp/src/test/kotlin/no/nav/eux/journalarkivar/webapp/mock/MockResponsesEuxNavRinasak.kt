package no.nav.eux.journalarkivar.webapp.mock

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

fun getEuxNavRinasakResponse(rinasakId: Int) =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(getNavRinasakResponseJson(rinasakId))
    }

fun postSedJournalstatuserFinnResponse(body: String): MockResponse {
    val status = ObjectMapper()
        .readTree(body)
        .findValue("sedJournalstatus")
        ?.asText()
    val responseBody = when (status) {
        "FEILET_FERDIGSTILL" -> sedJournalstatuserFinnResponseBody("FEILET_FERDIGSTILL")
        "FEILET_FEILREGISTRER" -> sedJournalstatuserFinnResponseBody("FEILET_FEILREGISTRER")
        else -> postSedJournalstatuserFinnResponseBody
    }
    return MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(responseBody)
    }
}

fun getNavRinasakResponseJson(rinasakId: Int) =
    Any::class::class.java
        .getResource("/dataset/eux-nav-rinasak/get-response-body-$rinasakId.json")!!
        .readText()

fun sedJournalstatuserFinnResponseBody(status: String) =
    Any::class::class.java
        .getResource("/dataset/eux-nav-rinasak/post-sed-journalstatuser-finn-response-body-$status.json")!!
        .readText()

val postSedJournalstatuserFinnResponseBody =
    Any::class::class.java
        .getResource("/dataset/eux-nav-rinasak/post-sed-journalstatuser-finn-response-body.json")!!
        .readText()
