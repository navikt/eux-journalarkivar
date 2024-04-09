package no.nav.eux.journalarkivar.integration.eux.journal.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate
import java.net.URI

@Component
class EuxJournalClient(
    @Value("\${endpoint.euxjournal}")
    val journalUrl: String,
    val euxJournalRestTemplate: RestTemplate
) {

    fun ferdigstill(journalpostId: String) {
        val request = RequestEntity
            .patch(URI("${journalUrl}/api/v1/journalposter/$journalpostId/ferdigstill"))
            .accept(MediaType.APPLICATION_JSON)
            .build()
        euxJournalRestTemplate.exchange(request, Void::class.java)
    }

    fun settStatusAvbryt(journalpostIder: List<String>) {
        RestClient.create(euxJournalRestTemplate)
            .post()
            .uri("${journalUrl}/api/v1/journalposter/settStatusAvbryt")
            .contentType(MediaType.APPLICATION_JSON)
            .body(SettStatusAvbrytRequestOpenApiType(journalpostIder))
            .retrieve()
            .toBodilessEntity()
    }
}

data class SettStatusAvbrytRequestOpenApiType(
    val journalpostIder: List<String>
)
