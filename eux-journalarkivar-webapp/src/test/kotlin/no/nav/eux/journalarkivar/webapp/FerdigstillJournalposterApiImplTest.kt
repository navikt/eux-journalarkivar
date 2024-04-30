package no.nav.eux.journalarkivar.webapp

import org.assertj.core.api.Assertions.assertThat
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
        assertThat(requestBodies["/api/v1/journalposter/453802638/ferdigstill"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/1444520"]).isNotNull()
        assertThat(requestBodies["/api/v1/journalposter/453802639/ferdigstill"]).isNull()
        assertThat(requestBodies["/api/v1/journalposter/453802640/ferdigstill"]).isNull()
        assertThat(requestBodies["/api/v1/rinasaker/1444520"]).isNotNull()
        assertThat(requestBodies["/api/v1/oppgaver/ferdigstill"])
            .isEqualTo("{\"journalpostIder\":[\"453802638\"],\"personident\":\"1662008437481\"}")
    }
}
