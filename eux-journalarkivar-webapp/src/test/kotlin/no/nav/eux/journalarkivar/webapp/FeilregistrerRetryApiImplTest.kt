package no.nav.eux.journalarkivar.webapp

import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import no.nav.eux.journalarkivar.webapp.common.token
import org.junit.jupiter.api.Test

class FeilregistrerRetryApiImplTest : AbstractApiImplTest() {

    @Test
    fun `feilregistrer retry - FEILET_FEILREGISTRER item fails again and becomes KORRUPT`() {
        restTestClient
            .post()
            .uri("/api/v1/arkivarprosess/feilregistrer/execute")
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .exchange()
        println("Følgende requests ble utført:")
        requestBodies.forEach { println("Path: ${it.key}, body: ${it.value}") }
        val putBodies = requestBodies["/api/v1/sed/journalstatuser"]
        putBodies shouldNotBe null
        val korruptPut = putBodies!!.firstOrNull { it.contains("KORRUPT") }
        korruptPut shouldNotBe null
        korruptPut!! shouldEqual "/dataset/forventet/retry-feilregistrer-korrupt.json"
        korruptPut shouldContain "feilmelding"
    }
}
