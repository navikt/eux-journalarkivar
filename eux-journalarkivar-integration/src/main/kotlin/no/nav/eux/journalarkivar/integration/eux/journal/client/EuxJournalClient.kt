package no.nav.eux.journalarkivar.integration.eux.journal.client

import no.nav.eux.journalarkivar.integration.config.patch
import no.nav.eux.journalarkivar.integration.config.post
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class EuxJournalClient(
    @param:Value("\${endpoint.euxjournal}")
    val journalUrl: String,
    val euxJournalRestTemplate: RestTemplate
) {

    fun ferdigstill(journalpostId: String) {
        euxJournalRestTemplate
            .patch()
            .uri("${journalUrl}/api/v1/journalposter/$journalpostId/ferdigstill")
            .accept(APPLICATION_JSON)
            .retrieve()
            .toBodilessEntity()
    }

    fun settStatusAvbryt(journalpostIder: List<String>) {
        euxJournalRestTemplate
            .post()
            .uri("${journalUrl}/api/v1/journalposter/settStatusAvbryt")
            .contentType(APPLICATION_JSON)
            .body(SettStatusAvbrytRequestOpenApiType(journalpostIder))
            .retrieve()
            .toBodilessEntity()
    }

    infix fun settStatusAvbrytFor(journalpostId: String) =
        settStatusAvbryt(listOf(journalpostId))

}

data class SettStatusAvbrytRequestOpenApiType(
    val journalpostIder: List<String>
)
