package no.nav.eux.journalarkivar.integration.eux.navrinasak.client

import no.nav.eux.journalarkivar.integration.config.get
import no.nav.eux.journalarkivar.integration.config.post
import no.nav.eux.journalarkivar.integration.config.put
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.*
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus.Status.UKJENT
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.toEntity

@Component
class EuxNavRinasakClient(
    @Value("\${endpoint.euxnavrinasak}")
    val euxNavRinasakUrl: String,
    val euxNavRinasakRestTemplate: RestTemplate
) {

    fun finn(euxSedJournalstatus: EuxSedJournalstatus.Status = UKJENT): List<EuxSedJournalstatus> {
        val entity: ResponseEntity<SedJournalstatuserSearchResponseType> = euxNavRinasakRestTemplate
            .post()
            .uri("${euxNavRinasakUrl}/api/v1/sed/journalstatuser/finn")
            .body(SedJournalstatusSearchCriteriaType(euxSedJournalstatus))
            .retrieve()
            .toEntity()
        return entity.body!!.sedJournalstatuser
    }

    fun finn(rinasakId: Int): EuxNavRinasak {
        val entity: ResponseEntity<EuxNavRinasak> = euxNavRinasakRestTemplate
            .get()
            .uri("${euxNavRinasakUrl}/api/v1/rinasaker/${rinasakId}")
            .retrieve()
            .toEntity()
        if (!entity.statusCode.is2xxSuccessful)
            throw RuntimeException("Fant ikke rinasak $rinasakId på: $euxNavRinasakUrl")
        return entity.body!!
    }

    fun put(journalstatusPut: EuxSedJournalstatusPut) {
        euxNavRinasakRestTemplate
            .put()
            .uri("${euxNavRinasakUrl}/api/v1/sed/journalstatuser")
            .body(journalstatusPut)
            .retrieve()
            .toBodilessEntity()
    }
}
