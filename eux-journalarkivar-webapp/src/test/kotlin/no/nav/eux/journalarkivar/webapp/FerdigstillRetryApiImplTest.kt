package no.nav.eux.journalarkivar.webapp

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.springframework.boot.resttestclient.exchange
import org.springframework.http.HttpMethod

class FerdigstillRetryApiImplTest : AbstractApiImplTest() {

    @Test
    fun `ferdigstill retry - FEILET_FERDIGSTILL item fails again and becomes KORRUPT`() {
        restTemplate
            .exchange<Void>(
                "/api/v1/arkivarprosess/ferdigstill/execute",
                HttpMethod.POST,
                httpEntity()
            )
        println("Følgende requests ble utført:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        val putBodies = requestBodies["/api/v1/sed/journalstatuser"]
        putBodies shouldNotBe null
        val korruptPut = putBodies!!.firstOrNull { it.contains("KORRUPT") }
        korruptPut shouldNotBe null
        korruptPut!! shouldEqual "/dataset/forventet/retry-ferdigstill-korrupt.json"
        korruptPut shouldContain "feilmelding"
    }

    @Test
    fun `ferdigstill retry - happy path items still get JOURNALFOERT`() {
        restTemplate
            .exchange<Void>(
                "/api/v1/arkivarprosess/ferdigstill/execute",
                HttpMethod.POST,
                httpEntity()
            )
        val putBodies = requestBodies["/api/v1/sed/journalstatuser"]
        putBodies shouldNotBe null
        val journalfoertCount = putBodies!!.count { it.contains("JOURNALFOERT") }
        (journalfoertCount > 0) shouldBe true
    }
}
