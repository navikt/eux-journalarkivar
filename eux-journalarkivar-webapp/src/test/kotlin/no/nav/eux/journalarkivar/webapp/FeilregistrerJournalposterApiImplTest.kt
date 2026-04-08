package no.nav.eux.journalarkivar.webapp

import no.nav.eux.journalarkivar.webapp.common.token
import org.junit.jupiter.api.Test

class FeilregistrerJournalposterApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST arkivarprosess - 204`() {
        restTestClient
            .post()
            .uri("/api/v1/arkivarprosess/feilregistrer/execute")
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .exchange()
        println("Følgende requests ble utført:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        "/api/v1/journalposter/settStatusAvbryt" requestNumber
                0 shouldEqual "/dataset/forventet/feilregistrer-journalpostIder.json"
    }
}
