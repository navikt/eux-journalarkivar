package no.nav.eux.journalarkivar.webapp

import io.kotest.matchers.string.shouldContain
import no.nav.eux.journalarkivar.webapp.common.arkivarprosessUrl
import no.nav.eux.journalarkivar.webapp.common.token
import no.nav.eux.journalarkivar.webapp.mock.sedJournalstatus
import org.junit.jupiter.api.Test

class FeilregistrerRetryApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST arkivarprosess feilregistrer - ukjent sed feiler, settes til feilet feilregistrer - 204`() {
        feilregistrerJournalposter()

        val feiletPut = sedJournalstatusPut("FEILET_FEILREGISTRER")
        feiletPut shouldEqual "/dataset/forventet/feilregistrer-feilet.json"
        feiletPut shouldContain "/api/v1/rinasaker/9999999"
    }

    @Test
    fun `POST arkivarprosess feilregistrer - feilet feilregistrer feiler igjen, settes til korrupt - 204`() {
        feilregistrerJournalposter()

        val korruptPut = sedJournalstatusPut("KORRUPT")
        korruptPut shouldEqual "/dataset/forventet/retry-feilregistrer-korrupt.json"
        korruptPut shouldContain "/api/v1/rinasaker/9999999"
    }

    private fun sedJournalstatusPut(journalstatus: String) =
        "/api/v1/sed/journalstatuser".requests.single { it.sedJournalstatus == journalstatus }

    private fun feilregistrerJournalposter() =
        restTestClient
            .post()
            .uri(arkivarprosessUrl, "feilregistrer")
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .exchange()
            .expectStatus().isNoContent
}
