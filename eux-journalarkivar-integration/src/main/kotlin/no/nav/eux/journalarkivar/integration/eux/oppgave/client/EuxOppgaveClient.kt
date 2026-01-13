package no.nav.eux.journalarkivar.integration.eux.oppgave.client

import no.nav.eux.journalarkivar.integration.config.post
import no.nav.eux.journalarkivar.integration.eux.oppgave.model.BehandleSedFraJournalpostId
import no.nav.eux.journalarkivar.integration.eux.oppgave.model.FerdigstillOppgaver
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class EuxOppgaveClient(
    @param:Value("\${endpoint.euxoppgave}")
    val euxOppgaveUrl: String,
    val euxOppgaveRestTemplate: RestTemplate
) {

    @Retryable(maxRetries = 8, delay = 1000, multiplier = 2.0)
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

    @Retryable(maxRetries = 8, delay = 1000, multiplier = 2.0)
    fun behandleSed(journalpostId: String, personident: String) {
        euxOppgaveRestTemplate
            .post()
            .uri("${euxOppgaveUrl}/api/v1/oppgaver/behandleSedFraJournalpostId")
            .body(
                BehandleSedFraJournalpostId(
                    journalpostId = journalpostId,
                    personident = personident
                )
            )
            .contentType(APPLICATION_JSON)
            .accept(MediaType.ALL)
            .retrieve()
            .toBodilessEntity()
    }

}
