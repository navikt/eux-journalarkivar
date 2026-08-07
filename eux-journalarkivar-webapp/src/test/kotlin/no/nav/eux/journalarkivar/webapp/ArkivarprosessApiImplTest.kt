package no.nav.eux.journalarkivar.webapp

import io.kotest.matchers.maps.shouldBeEmpty
import no.nav.eux.journalarkivar.webapp.common.arkivarprosessUrl
import no.nav.eux.journalarkivar.webapp.common.token
import org.junit.jupiter.api.Test

class ArkivarprosessApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST arkivarprosess - ukjent prosess - 400`() {
        restTestClient
            .post()
            .uri(arkivarprosessUrl, "ukjentProsess")
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .exchange()
            .expectStatus().isBadRequest

        requestBodies.shouldBeEmpty()
    }
}
