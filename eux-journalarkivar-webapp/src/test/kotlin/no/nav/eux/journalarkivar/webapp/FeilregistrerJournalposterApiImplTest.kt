package no.nav.eux.journalarkivar.webapp

import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpMethod

class FeilregistrerJournalposterApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST arkivarprosess - 204`() {
        restTemplate
            .exchange<Void>(
                "/api/v1/arkivarprosess/feilregistrer/execute",
                HttpMethod.POST,
                httpEntity()
            )
        println("Følgende requests ble utført:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        "/api/v1/journalposter/settStatusAvbryt" requestNumber
                0 shouldEqual "/dataset/forventet/feilregistrer-journalpostIder.json"
    }
}
