package no.nav.eux.journalarkivar.integration.external.dokarkiv.client

import no.nav.eux.journalarkivar.integration.config.put
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivJournalpostAvsenderMottakerOppdatering
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivJournalpostAvsenderMottakerOppdatering.AvsenderMottaker
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivJournalpostOppdatering
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivOppdatering
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class DokarkivClient(
    @param:Value("\${endpoint.dokarkiv}")
    val dokarkivUrl: String,
    val dokarkivRestTemplate: RestTemplate
) {

    val uri: String by lazy { "$dokarkivUrl/rest/journalpostapi/v1/journalpost" }

    fun oppdater(
        journalpostId: String,
        dokarkivJournalpostOppdatering: DokarkivJournalpostOppdatering
    ) {
        oppdaterJournalpost(journalpostId, dokarkivJournalpostOppdatering)
    }

    fun oppdater(
        journalpostId: String,
        avsenderMottakerNavn: String,
    ) {
        val body = DokarkivJournalpostAvsenderMottakerOppdatering(
            avsenderMottaker = AvsenderMottaker(avsenderMottakerNavn)
        )
        oppdaterJournalpost(journalpostId, body)
    }

    private fun oppdaterJournalpost(
        journalpostId: String,
        dokarkivOppdatering: DokarkivOppdatering,
    ) {
        dokarkivRestTemplate
            .put()
            .uri("$uri/$journalpostId")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(dokarkivOppdatering)
            .retrieve()
            .toBodilessEntity()
    }

}
