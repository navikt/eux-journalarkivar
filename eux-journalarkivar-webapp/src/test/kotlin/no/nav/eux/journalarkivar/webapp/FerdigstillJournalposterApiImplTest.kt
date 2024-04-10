package no.nav.eux.journalarkivar.webapp

import no.nav.eux.journalarkivar.webapp.common.ferdigstillJournalposterUrl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.exchange
import org.springframework.http.HttpMethod

class FerdigstillJournalposterApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST ferdigstillJournalposter - 204`() {
        val createResponse = restTemplate
            .exchange<Void>(
                ferdigstillJournalposterUrl,
                HttpMethod.POST,
                "{}".httpEntity
            )
        println("Følgende requests ble utført:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        assertThat(requestBodies["/api/v1/journalposter/453802638/ferdigstill"]).isNotNull()
        assertThat(requestBodies["/api/v1/rinasaker/1444520"]).isNotNull()

    }
}
