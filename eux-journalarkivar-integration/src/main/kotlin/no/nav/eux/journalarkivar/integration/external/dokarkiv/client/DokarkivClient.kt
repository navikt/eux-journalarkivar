package no.nav.eux.journalarkivar.integration.external.dokarkiv.client

import no.nav.eux.journalarkivar.integration.config.put
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivJournalpostOppdatering
import no.nav.eux.journalarkivar.integration.external.dokarkiv.model.DokarkivJournalpostSakOppdatering
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class DokarkivClient(
    @Value("\${endpoint.dokarkiv}")
    val dokarkivUrl: String,
    val dokarkivRestTemplate: RestTemplate
) {

    val uri: String by lazy { "$dokarkivUrl/rest/journalpostapi/v1/journalpost" }

    fun oppdater(
        journalpostId: String,
        dokarkivJournalpostOppdatering: DokarkivJournalpostOppdatering
    ) {
        dokarkivRestTemplate
            .put()
            .uri("$uri/$journalpostId")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(dokarkivJournalpostOppdatering)
            .retrieve()
            .toBodilessEntity()
    }

    fun oppdater(
        journalpostId: String,
        dokarkivJournalpostOppdatering: DokarkivJournalpostSakOppdatering
    ) {
        dokarkivRestTemplate
            .put()
            .uri("$uri/$journalpostId")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(dokarkivJournalpostOppdatering)
            .retrieve()
            .toBodilessEntity()
    }
}
