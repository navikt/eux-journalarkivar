package no.nav.eux.journalarkivar.integration.external.saf.client

import no.nav.eux.journalarkivar.integration.config.post
import no.nav.eux.journalarkivar.integration.external.saf.model.SafJournalpost
import no.nav.eux.journalarkivar.integration.external.saf.model.SafRoot
import no.nav.eux.journalarkivar.integration.external.saf.model.SafTilknyttedeJournalposterData
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.body

@Component
class SafClient(
    @param:Value("\${endpoint.saf}")
    val safUrl: String,
    val safRestTemplate: RestTemplate
) {

    @Retryable(maxRetries = 8, delay = 1000, multiplier = 2.0)
    fun firstTilknyttetJournalpostOrNull(dokumentInfoId: String): SafJournalpost? =
        safRestTemplate
            .post()
            .uri("$safUrl/graphql")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(tilknyttedeJournalposterQuery(dokumentInfoId))
            .retrieve()
            .body<SafRoot<SafTilknyttedeJournalposterData>>()
            .safData()
            .tilknyttedeJournalposter
            .firstOrNull()

    fun <T> SafRoot<T>?.safData(): T =
        when (this!!.data) {
            null -> throw RuntimeException("Feil fra SAF: ${errors?.joinToString { "${it.message}" }}")
            else -> data
        }
}

fun tilknyttedeJournalposterQuery(dokumentInfoId: String) = GraphQlQuery(
    """query {
          tilknyttedeJournalposter(dokumentInfoId: "$dokumentInfoId", tilknytning: GJENBRUK) {
              journalpostId
              tittel
              journalposttype
              journalstatus
              eksternReferanseId
              tema
              dokumenter {
                dokumentInfoId
                tittel
                brevkode
              }
              tilleggsopplysninger {
                nokkel
                verdi
              }
              sak {
                tema
                fagsakId
                fagsaksystem
                arkivsaksnummer
                arkivsaksystem
                sakstype
              }
              bruker {
                id
                type
              }
              avsenderMottaker {
                navn
              }
          }
        }""".trimIndent()
)

data class GraphQlQuery(
    val query: String
)
