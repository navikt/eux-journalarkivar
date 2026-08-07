package no.nav.eux.journalarkivar.webapp

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import no.nav.eux.journalarkivar.webapp.common.arkivarprosessUrl
import no.nav.eux.journalarkivar.webapp.common.token
import no.nav.eux.journalarkivar.webapp.mock.sedJournalstatus
import org.junit.jupiter.api.Test

class FeilregistrerJournalposterApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST arkivarprosess feilregistrer - utgående journalpost uten bruker feilregistreres - 204`() {
        feilregistrerJournalposter()

        "/api/v1/journalposter/settStatusAvbryt".requests shouldEqual
                "/dataset/forventet/feilregistrer-settStatusAvbryt.json"
        "/api/v1/sed/journalstatuser".requests.single { it.sedJournalstatus == "FEILREGISTRERT" } shouldEqual
                "/dataset/forventet/feilregistrer-feilregistrert.json"
    }

    @Test
    fun `POST arkivarprosess feilregistrer - sed opprettet siste 30 dager hoppes over - 204`() {
        feilregistrerJournalposter()

        "/api/v1/rinasaker/1444522".requests shouldHaveSize 1
        "/api/v1/rinasaker/1444523".requests.shouldBeEmpty()
    }

    @Test
    fun `POST arkivarprosess feilregistrer - sed journalstatuser oppdateres - 204`() {
        feilregistrerJournalposter()

        "/api/v1/sed/journalstatuser".requests shouldEqual
                "/dataset/forventet/feilregistrer-sed-journalstatuser.json"
    }

    private fun feilregistrerJournalposter() =
        restTestClient
            .post()
            .uri(arkivarprosessUrl, "feilregistrer")
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .exchange()
            .expectStatus().isNoContent
}
