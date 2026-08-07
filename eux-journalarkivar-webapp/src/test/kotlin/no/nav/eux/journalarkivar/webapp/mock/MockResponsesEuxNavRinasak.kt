package no.nav.eux.journalarkivar.webapp.mock

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.time.OffsetDateTime.now

fun getEuxNavRinasakResponse(rinasakId: Int) =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(getNavRinasakResponseJson(rinasakId))
    }

fun postSedJournalstatuserFinnResponse(body: String) =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(sedJournalstatuserFinnResponseBody(body.sedJournalstatus))
    }

fun getNavRinasakResponseJson(rinasakId: Int) =
    Any::class::class.java
        .getResource("/dataset/eux-nav-rinasak/get-response-body-$rinasakId.json")!!
        .readText()

fun sedJournalstatuserFinnResponseBody(status: String) =
    Any::class::class.java
        .getResource("/dataset/eux-nav-rinasak/post-sed-journalstatuser-finn-response-body-$status.json")!!
        .readText()
        .replace(nyligOpprettetTidspunktPlaceholder, now().toString())

const val nyligOpprettetTidspunktPlaceholder = "NYLIG_OPPRETTET_TIDSPUNKT"

private val objectMapper = ObjectMapper()

val String.sedJournalstatus: String
    get() = objectMapper
        .readTree(this)
        .findValue("sedJournalstatus")
        ?.asText()
        ?: throw RuntimeException("Fant ikke sedJournalstatus i søkekriterier: $this")

val String.rinasakId: Int
    get() = objectMapper
        .readTree(this)
        .findValue("rinasakId")
        ?.asInt()
        ?: throw RuntimeException("Fant ikke rinasakId: $this")
