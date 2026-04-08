package no.nav.eux.journalarkivar.webapp

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import no.nav.eux.journalarkivar.webapp.common.token
import org.junit.jupiter.api.Test

class FerdigstillJournalposterApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST arkivarprosess - 204`() {
        restTestClient
            .post()
            .uri("/api/v1/arkivarprosess/ferdigstill/execute")
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .exchange()
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
