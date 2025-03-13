package no.nav.eux.journalarkivar.webapp.mock

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType.TEXT_PLAIN

fun mockResponse(request: RecordedRequest, body: String) =
    when (request.method) {
        HttpMethod.POST.name() -> mockResponsePost(request, body)
        HttpMethod.GET.name() -> mockResponseGet(request)
        HttpMethod.PATCH.name() -> mockResponsePatch(request)
        HttpMethod.PUT.name() -> mockResponsePut(request)
        else -> defaultResponse()
    }

fun mockResponsePost(request: RecordedRequest, body: String) =
    when (request.uriEndsWith) {
        "/oauth2/v2.0/token" -> tokenResponse()
        "/api/v1/sed/journalstatuser/finn" -> postSedJournalstatuserFinnResponse()
        "/graphql" -> safResponse(body)
        "/api/v1/oppgaver" -> oppgaverResponse()
        "/api/v1/oppgaver/tildelEnhetsnr" -> tildelEnhetsnrResponse(body)
        "/api/v1/journalposter/settStatusAvbryt" -> response200()
        "/api/v1/oppgaver/ferdigstill" -> response200()
        "/api/v1/oppgaver/behandleSedFraJournalpostId" -> response200()
        else -> defaultResponse()
    }

fun mockResponsePatch(request: RecordedRequest) =
    when (request.uriEndsWith) {
        "/journalpost/453802638/feilregistrer/settStatusAvbryt" -> response200()
        "/journalpost/453802638/ferdigstill" -> response200()
        "/api/v1/journalposter/453802638/ferdigstill" -> response200()
        "/api/v1/oppgaver/190402" -> oppgaverResponse()
        else -> defaultResponse()
    }

fun mockResponseGet(request: RecordedRequest) =
    when (request.uriEndsWith) {
        "/api/v1/rinasaker/1444520" -> getEuxNavRinasakResponse(1444520)
        "/api/v1/rinasaker/1444521" -> getEuxNavRinasakResponse(1444521)
        "/api/v1/rinasaker/1444522" -> getEuxNavRinasakResponse(1444522)
        "/v3/buc/1444520/oversikt" -> getRinaApiResponse()
        "/v3/buc/1444521/oversikt" -> getRinaApiResponse()
        "/v3/buc/1444522/oversikt" -> getRinaApiResponse()
        getOppgaverUri -> getOppgaverResponse()
        else -> defaultResponse()
    }

fun mockResponsePut(request: RecordedRequest) =
    when (request.uriEndsWith) {
        "/rest/journalpostapi/v1/journalpost/453802638" -> response200()
        "/api/v1/sed/journalstatuser" -> response200()
        else -> defaultResponse()
    }

fun defaultResponse() =
    MockResponse().apply {
        setHeader(CONTENT_TYPE, TEXT_PLAIN)
        setBody("no mock defined")
        setResponseCode(500)
    }

val RecordedRequest.uriEndsWith get() = requestUrl.toString().split("/mock")[1]
