package no.nav.eux.journalarkivar.integration.external.saf.client

import no.nav.eux.journalarkivar.integration.config.post
import no.nav.eux.journalarkivar.integration.external.saf.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.body

@Component
class SafClient(
    @Value("\${endpoint.saf}")
    val safUrl: String,
    val safRestTemplate: RestTemplate
) {

    fun dokumentoversiktBrukerRoot(fnr: String): List<SafJournalpost> =
        safRestTemplate
            .post()
            .uri("$safUrl/graphql")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(dokumentoversiktBrukerQuery(fnr))
            .retrieve()
            .body<SafDokumentoversiktBrukerRoot>()
            ?.data
            ?.dokumentoversiktBruker
            ?.journalposter
            ?: emptyList()

    fun safSaker(fnr: String): List<SafSak> =
        safRestTemplate
            .post()
            .uri("$safUrl/graphql")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(sakerQuery(fnr))
            .retrieve()
            .body<SafSakerRoot>()
            ?.data
            ?.saker
            ?: emptyList()

    fun safJournalpostOrNull(journalpostId: String): SafJournalpost? =
        safRestTemplate
            .post()
            .uri("$safUrl/graphql")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(journalpostQuery(journalpostId))
            .retrieve()
            .body<SafJournalpostRoot>()
            ?.data
            ?.journalpost

    fun tilknyttedeJournalposter(dokumentInfoId: String): List<SafJournalpost> =
        tilknyttedeJournalposterRoot(dokumentInfoId)
            ?.data
            ?.tilknyttedeJournalposter
            ?: emptyList()

    fun firstTilknyttetJournalpostOrNull(dokumentInfoId: String): SafJournalpost? =
        tilknyttedeJournalposter(dokumentInfoId)
            .firstOrNull()

    fun tilknyttedeJournalposterRoot(dokumentInfoId: String) =
        safRestTemplate
            .post()
            .uri("$safUrl/graphql")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(tilknyttedeJournalposterQuery(dokumentInfoId))
            .retrieve()
            .body<SafTilknyttedeJournalposterRoot>()
}

fun journalpostQuery(journalpostId: String) = GraphQlQuery(
    """query {
          journalpost(journalpostId: "$journalpostId") {
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
          }
        }""".trimIndent()
)

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
          }
        }""".trimIndent()
)

fun dokumentoversiktBrukerQuery(fnr: String) = GraphQlQuery(
    """query {
          dokumentoversiktBruker(
            brukerId: { id: "$fnr", type:FNR },
            journalstatuser: [],
            journalposttyper: [],
            foerste: 1000
          ) {
            journalposter {
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
             }
          }
        }""".trimIndent()
)

fun sakerQuery(fnr: String) = GraphQlQuery(
    """query {
          saker(brukerId: { id: "$fnr", type:FNR } ) {
            tema
            fagsakId
            fagsaksystem
            arkivsaksnummer
            arkivsaksystem
            sakstype
          }
        }""".trimIndent()
)

data class GraphQlQuery(
    val query: String
)
