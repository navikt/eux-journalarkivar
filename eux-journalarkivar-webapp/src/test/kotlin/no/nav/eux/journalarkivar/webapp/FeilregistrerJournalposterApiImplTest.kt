package no.nav.eux.journalarkivar.webapp

import no.nav.eux.journalarkivar.webapp.common.feilregistrerJournalposterUrl
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpMethod

class FeilregistrerJournalposterApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST feilregistrerJournalposter - 204`() {
        restTemplate
            .exchange<Void>(
                feilregistrerJournalposterUrl,
                HttpMethod.POST,
                "{}".httpEntity
            )
        println("Følgende requests ble utført:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
//        assertThat(requestBodies["/api/v1/journalposter/settStatusAvbryt"])
//            .isEqualTo("{\"journalpostIder\":[\"453802638\"]}")
    }
}
