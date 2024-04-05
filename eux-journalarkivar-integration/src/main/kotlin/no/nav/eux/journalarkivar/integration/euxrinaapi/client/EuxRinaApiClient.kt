package no.nav.eux.journalarkivar.integration.euxrinaapi.client

import no.nav.eux.journalarkivar.integration.euxrinaapi.model.EuxRinaSakOversiktV3
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForEntity
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@Component
class EuxRinaApiClient(
    @Value("\${endpoint.euxrinaapi}")
    val euxRinaApiUrl: String,
    val euxRinaApiRestTemplate: RestTemplate
) {
    val log: Logger = LoggerFactory.getLogger(EuxRinaApiClient::class.java)

    fun euxRinaSakOversikt(rinaSakId: Int): EuxRinaSakOversiktV3 {
        val (entity, duration) = measureTimedValue {
            val entity: ResponseEntity<EuxRinaSakOversiktV3> = euxRinaApiRestTemplate
                .getForEntity("${euxRinaApiUrl}/v3/buc/$rinaSakId/oversikt")
            entity
        }
        log.info("Det tok ${duration.inWholeMilliseconds} milliseconds å hente ut rina oversikt for fnr")
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Kunne ikke hente fnr fra eux rina api, status code: ${entity.statusCode}")
        return entity.body!!
    }

    fun fnrOrNull(rinaSakId: Int) = try {
        euxRinaSakOversikt(rinaSakId).fnr
    } catch (e: RuntimeException) {
        log.error("Kunne ikke hente rinaSakId: $rinaSakId", e)
        null
    }
}
