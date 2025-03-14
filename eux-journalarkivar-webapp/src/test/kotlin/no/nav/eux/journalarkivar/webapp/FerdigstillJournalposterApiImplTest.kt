package no.nav.eux.journalarkivar.webapp

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpMethod

class FerdigstillJournalposterApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST arkivarprosess - 204`() {
        restTemplate
            .exchange<Void>(
                "/api/v1/arkivarprosess/ferdigstill/execute",
                HttpMethod.POST,
                httpEntity()
            )
        println("Følgende requests ble utført:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        "/api/v1/journalposter/453802638/ferdigstill" requestNumber 0 shouldNotBe null
        "/api/v1/rinasaker/1444520" requestNumber 0 shouldNotBe null
        "/api/v1/journalposter/453802639/ferdigstill" requestNumber 0 shouldBe null
        "/api/v1/journalposter/453802640/ferdigstill" requestNumber 0 shouldBe null
        "/api/v1/rinasaker/1444520" requestNumber 0 shouldNotBe null
        "/api/v1/oppgaver/ferdigstill" requestNumber 0 shouldEqual "/dataset/forventet/ferdigstill-journalpostIder.json"
        "/rest/journalpostapi/v1/journalpost/453802641" requestNumber
                0 shouldEqual "/dataset/forventet/ferdigstill-oppdater-avsenderMottaker.json"
        "/rest/journalpostapi/v1/journalpost/453802641" requestNumber
                1 shouldEqual "/dataset/forventet/ferdigstill-oppdater-avsenderMottaker.json"
    }
}
