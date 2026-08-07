package no.nav.eux.journalarkivar.webapp

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import no.nav.eux.journalarkivar.webapp.common.arkivarprosessUrl
import no.nav.eux.journalarkivar.webapp.common.token
import no.nav.eux.journalarkivar.webapp.mock.dokumentInfoId
import no.nav.eux.journalarkivar.webapp.mock.rinasakId
import no.nav.eux.journalarkivar.webapp.mock.sedJournalstatus
import org.junit.jupiter.api.Test

class FerdigstillJournalposterApiImplTest : AbstractApiImplTest() {

    @Test
    fun `POST arkivarprosess ferdigstill - inngående journalpost ferdigstilles - 204`() {
        ferdigstillJournalposter()

        "/rest/journalpostapi/v1/journalpost/453802638".requests shouldHaveSize 2
        "/rest/journalpostapi/v1/journalpost/453802638" requestNumber
                0 shouldEqual "/dataset/forventet/ferdigstill-oppdater-avsenderMottaker-inngaaende.json"
        "/rest/journalpostapi/v1/journalpost/453802638" requestNumber
                1 shouldEqual "/dataset/forventet/ferdigstill-oppdater-journalpost-generell-sak.json"
        "/api/v1/journalposter/453802638/ferdigstill".requests shouldHaveSize 1
        "/api/v1/oppgaver/ferdigstill".requests shouldEqual
                "/dataset/forventet/ferdigstill-oppgaver-ferdigstill.json"
        "/api/v1/oppgaver/behandleSedFraJournalpostId".requests shouldEqual
                "/dataset/forventet/ferdigstill-oppgaver-behandle-sed.json"
    }

    @Test
    fun `POST arkivarprosess ferdigstill - utgående journalpost med fagsak ferdigstilles - 204`() {
        ferdigstillJournalposter()

        "/rest/journalpostapi/v1/journalpost/453802642".requests shouldHaveSize 1
        "/rest/journalpostapi/v1/journalpost/453802642" requestNumber
                0 shouldEqual "/dataset/forventet/ferdigstill-oppdater-journalpost-fagsak.json"
        "/api/v1/journalposter/453802642/ferdigstill".requests shouldHaveSize 1
    }

    @Test
    fun `POST arkivarprosess ferdigstill - journalpost er allerede journalført - 204`() {
        ferdigstillJournalposter()

        "/rest/journalpostapi/v1/journalpost/453802640".requests.shouldBeEmpty()
        "/api/v1/journalposter/453802640/ferdigstill".requests.shouldBeEmpty()
    }

    @Test
    fun `POST arkivarprosess ferdigstill - feilregistrert sed forblir feilregistrert - 204`() {
        ferdigstillJournalposter()

        "/rest/journalpostapi/v1/journalpost/453802644".requests.shouldBeEmpty()
        "/api/v1/journalposter/453802644/ferdigstill".requests.shouldBeEmpty()
        "/api/v1/sed/journalstatuser".requests
            .single { it.rinasakId == 1444524 }
            .sedJournalstatus shouldBe "FEILREGISTRERT"
    }

    @Test
    fun `POST arkivarprosess ferdigstill - ukjent sed med feilregistrert journalpost synkroniseres - 204`() {
        ferdigstillJournalposter()

        "/rest/journalpostapi/v1/journalpost/453802645".requests.shouldBeEmpty()
        "/api/v1/journalposter/453802645/ferdigstill".requests.shouldBeEmpty()
        "/api/v1/sed/journalstatuser".requests.single { it.rinasakId == 1444525 } shouldEqual
                "/dataset/forventet/ferdigstill-feilregistrert.json"
    }

    @Test
    fun `POST arkivarprosess ferdigstill - saf spørring inneholder nødvendige felter - 204`() {
        ferdigstillJournalposter()

        "/graphql".requests.first { it.dokumentInfoId == "454221906" } shouldEqualGraphQlQuery
                "/dataset/forventet/saf-tilknyttede-journalposter-query.json"
    }

    @Test
    fun `POST arkivarprosess ferdigstill - sak uten ferdigstilte journalposter - 204`() {
        ferdigstillJournalposter()

        "/rest/journalpostapi/v1/journalpost/453802641".requests shouldHaveSize 1
        "/rest/journalpostapi/v1/journalpost/453802641" requestNumber
                0 shouldEqual "/dataset/forventet/ferdigstill-oppdater-avsenderMottaker-utgaaende.json"
        "/api/v1/journalposter/453802641/ferdigstill".requests.shouldBeEmpty()
    }

    @Test
    fun `POST arkivarprosess ferdigstill - sed journalstatuser oppdateres - 204`() {
        ferdigstillJournalposter()

        "/api/v1/sed/journalstatuser".requests shouldEqual
                "/dataset/forventet/ferdigstill-sed-journalstatuser.json"
    }

    private fun ferdigstillJournalposter() =
        restTestClient
            .post()
            .uri(arkivarprosessUrl, "ferdigstill")
            .header("Authorization", "Bearer ${mockOAuth2Server.token}")
            .exchange()
            .expectStatus().isNoContent
}
