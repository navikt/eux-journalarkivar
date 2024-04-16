package no.nav.eux.journalarkivar.integration.eux.oppgave.client

import no.nav.eux.journal.integration.client.eux.oppgave.TildelEnhetsnr
import no.nav.eux.journalarkivar.integration.config.post
import no.nav.eux.journalarkivar.integration.eux.oppgave.model.FerdigstillOppgaver
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class EuxOppgaveClient(
    @Value("\${endpoint.euxoppgave}")
    val euxOppgaveUrl: String,
    val euxOppgaveRestTemplate: RestTemplate
) {

    fun ferdigstillOppgave(journalpostId: String, personident: String) {
        euxOppgaveRestTemplate
            .post()
            .uri("${euxOppgaveUrl}/api/v1/oppgaver/ferdigstill")
            .body(
                FerdigstillOppgaver(
                    journalpostIder = listOf(journalpostId),
                    personident = personident,
                )
            )
            .contentType(APPLICATION_JSON)
            .accept(MediaType.ALL)
            .retrieve()
            .toBodilessEntity()
    }

    fun tildelEnhetsnr(journalpostId: String, enhetsnr: String, kommentar: String) {
        euxOppgaveRestTemplate
            .post()
            .uri("${euxOppgaveUrl}/api/v1/oppgaver/tildelEnhetsnr")
            .body(
                TildelEnhetsnr(
                    journalpostId = journalpostId,
                    tildeltEnhetsnr = enhetsnr,
                    kommentar = kommentar
                )
            )
            .contentType(APPLICATION_JSON)
            .accept(MediaType.ALL)
            .retrieve()
            .toBodilessEntity()
    }
}
