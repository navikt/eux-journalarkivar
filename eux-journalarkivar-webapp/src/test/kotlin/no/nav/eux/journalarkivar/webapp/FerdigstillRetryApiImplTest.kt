package no.nav.eux.journalarkivar.webapp

import io.kotest.matchers.string.shouldContain
import no.nav.eux.journalarkivar.webapp.common.arkivarprosessUrl
import no.nav.eux.journalarkivar.webapp.common.token
import no.nav.eux.journalarkivar.webapp.mock.sedJournalstatus
import org.junit.jupiter.api.Test

class FerdigstillRetryApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST arkivarprosess ferdigstill - ukjent sed feiler, settes til feilet ferdigstill - 204`() {
        ferdigstillJournalposter()

        val feiletPut = sedJournalstatusPut("FEILET_FERDIGSTILL")
        feiletPut shouldEqual "/dataset/forventet/ferdigstill-feilet.json"
        feiletPut shouldContain "/api/v1/rinasaker/9999999"
    }

    @Test
    fun `POST arkivarprosess ferdigstill - feilet ferdigstill feiler igjen, settes til korrupt - 204`() {
        ferdigstillJournalposter()

        val korruptPut = sedJournalstatusPut("KORRUPT")
        korruptPut shouldEqual "/dataset/forventet/retry-ferdigstill-korrupt.json"
        korruptPut shouldContain "/api/v1/rinasaker/9999999"
    }

    private fun sedJournalstatusPut(journalstatus: String) =
        "/api/v1/sed/journalstatuser".requests.single { it.sedJournalstatus == journalstatus }

    private fun ferdigstillJournalposter() =
        restTestClient
            .post()
            .uri(arkivarprosessUrl, "ferdigstill")
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .exchange()
            .expectStatus().isNoContent
}
