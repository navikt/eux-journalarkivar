package no.nav.eux.journalarkivar.integration.eux.rinaapi.client

import no.nav.eux.journalarkivar.integration.config.get
import no.nav.eux.journalarkivar.integration.eux.rinaapi.model.EuxRinaSakOversiktV3
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.body

@Component
class EuxRinaApiClient(
    @Value("\${endpoint.euxrinaapi}")
    val euxRinaApiUrl: String,
    val euxRinaApiRestTemplate: RestTemplate
) {

    @Retryable
    fun euxRinaSakOversikt(rinaSakId: Int): EuxRinaSakOversiktV3 =
        euxRinaApiRestTemplate
            .get()
            .uri("${euxRinaApiUrl}/v3/buc/$rinaSakId/oversikt")
            .accept(APPLICATION_JSON)
            .retrieve()
            .body<EuxRinaSakOversiktV3>()
            ?: throw RuntimeException("Kunne ikke hente rinasak med rinasakId $rinaSakId")
}
