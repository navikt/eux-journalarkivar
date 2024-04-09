package no.nav.eux.journalarkivar.integration.eux.navrinasak.client

import no.nav.eux.journalarkivar.integration.config.get
import no.nav.eux.journalarkivar.integration.config.post
import no.nav.eux.journalarkivar.integration.config.put
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.*
import no.nav.eux.journalarkivar.integration.eux.navrinasak.model.EuxSedJournalstatus.Status.UKJENT
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.body

@Component
class EuxNavRinasakClient(
    @Value("\${endpoint.euxnavrinasak}")
    val euxNavRinasakUrl: String,
    val euxNavRinasakRestTemplate: RestTemplate
) {

    fun finn(euxSedJournalstatus: EuxSedJournalstatus.Status = UKJENT) =
        euxNavRinasakRestTemplate
            .post()
            .uri("${euxNavRinasakUrl}/api/v1/sed/journalstatuser/finn")
            .body(SedJournalstatusSearchCriteriaType(euxSedJournalstatus))
            .retrieve()
            .body<SedJournalstatuserSearchResponseType>()!!
            .sedJournalstatuser


    fun finn(rinasakId: Int) =
        euxNavRinasakRestTemplate
            .get()
            .uri("${euxNavRinasakUrl}/api/v1/rinasaker/${rinasakId}")
            .retrieve()
            .body<EuxNavRinasak>()!!

    fun put(journalstatusPut: EuxSedJournalstatusPut) {
        euxNavRinasakRestTemplate
            .put()
            .uri("${euxNavRinasakUrl}/api/v1/sed/journalstatuser")
            .body(journalstatusPut)
            .retrieve()
            .toBodilessEntity()
    }
}
