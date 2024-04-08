package no.nav.eux.journalarkivar.integration.saf.client

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journalarkivar.integration.saf.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.http.RequestEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class SafClient(
    @Value("\${endpoint.saf}")
    val safUrl: String,
    val safRestTemplate: RestTemplate
) {

    val log = logger {}

    fun dokumentoversiktBrukerStringTest(fnr: String) {
        val graphQlQuery = dokumentoversiktBrukerQuery(fnr)
        val request = RequestEntity
            .post(
                UriComponentsBuilder
                    .fromHttpUrl(safUrl)
                    .path("/graphql").build().toUri()
            )
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body<GraphQlQuery>(graphQlQuery)
        log.info("Henter SAF tilknyttede journalposter for bruker")
        val response = safRestTemplate
            .exchange(request, String::class.java)
        return if (response.statusCode.is2xxSuccessful) {
            log.info { "Body fra Saf: ${response.body}" }
        } else {
            log.error("Feilet mot SAF (${response.statusCode.value()}), body: ${response.body}")
            throw RuntimeException("feilet mot saf med ${response.statusCode.value()}")
        }
    }

    fun dokumentoversiktBrukerRoot(fnr: String): SafDokumentoversiktBrukerRoot {
        val graphQlQuery = dokumentoversiktBrukerQuery(fnr)
        val request = RequestEntity
            .post(
                UriComponentsBuilder
                    .fromHttpUrl(safUrl)
                    .path("/graphql").build().toUri()
            )
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body<GraphQlQuery>(graphQlQuery)
        log.info("Henter SAF tilknyttede journalposter for bruker")
        val response = safRestTemplate
            .exchange(request, SafDokumentoversiktBrukerRoot::class.java)
        return if (response.statusCode.is2xxSuccessful) {
            response
                .body
                ?: throw RuntimeException("feilet mot saf tilknyttede journalposter, men med 200")
        } else {
            log.error("Feilet mot SAF (${response.statusCode.value()}), body: ${response.body}")
            throw RuntimeException("feilet mot saf med ${response.statusCode.value()}")
        }
    }

    fun safSakerRoot(fnr: String): SafSakerRoot? {
        val graphQlQuery = sakerQuery(fnr)
        val request = RequestEntity
            .post(
                UriComponentsBuilder
                    .fromHttpUrl(safUrl)
                    .path("/graphql").build().toUri()
            )
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body<GraphQlQuery>(graphQlQuery)
        val response = safRestTemplate
            .exchange(request, SafSakerRoot::class.java)
        return if (response.statusCode.is2xxSuccessful) {
            response.body
        } else {
            log.info("Feilet mot SAF (${response.statusCode.value()}), body: ${response.body}")
            throw RuntimeException("feilet mot saf med ${response.statusCode.value()}")
        }
    }

    fun safSaker(fnr: String): List<SafSak> =
        try {
            safSakerRoot(fnr)!!
                .data
                .saker
        } catch (e: RuntimeException) {
            log.error("Kunne ikke hente saker fra SAF på fnr", e)
            emptyList()
        }

    fun safSakOrNull(fnr: String, fagsakId: String): SafSak? {
        val saker = safSaker(fnr)
        val sakMedFagsakId = saker.firstOrNull { it.arkivsaksnummer == fagsakId }
        return if (saker.isEmpty()) {
            log.error("Tom liste fra SAF for fnr og fagsakId $fagsakId")
            null
        } else if (sakMedFagsakId != null) {
            sakMedFagsakId
        } else {
            log.error("Treff mot SAF saker på fnr (${saker.size}), men uten fagsakId $fagsakId")
            null
        }
    }

    fun safJournalpost(journalpostId: String): SafJournalpost {
        val graphQlQuery = journalpostQuery(journalpostId)
        val request = RequestEntity
            .post(
                UriComponentsBuilder
                    .fromHttpUrl(safUrl)
                    .path("/graphql").build().toUri()
            )
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body<GraphQlQuery>(graphQlQuery)
        val response = safRestTemplate
            .exchange(request, SafJournalpostRoot::class.java)
        return if (response.statusCode.is2xxSuccessful) {
            response
                .body
                ?.data
                ?.journalpost ?: throw RuntimeException("feilet mot saf, men med 200")
        } else {
            log.info("Feilet mot SAF (${response.statusCode.value()}), body: ${response.body}")
            throw RuntimeException("feilet mot saf med ${response.statusCode.value()}")
        }
    }

    fun firstTilknyttetJournalpostOrNull(dokumentInfoId: String): SafJournalpost? =
        tilknyttedeJournalposterRoot(dokumentInfoId)
            .data
            .tilknyttedeJournalposter
            .firstOrNull()

    fun tilknyttedeJournalposterRoot(dokumentInfoId: String): SafTilknyttedeJournalposterRoot {
        val graphQlQuery = tilknyttedeJournalposterQuery(dokumentInfoId)
        val request = RequestEntity
            .post(
                UriComponentsBuilder
                    .fromHttpUrl(safUrl)
                    .path("/graphql").build().toUri()
            )
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body<GraphQlQuery>(graphQlQuery)
        log.info("Henter SAF tilknyttede journalposter for dokument info id: $dokumentInfoId")
        val response = safRestTemplate
            .exchange(request, SafTilknyttedeJournalposterRoot::class.java)
        return if (response.statusCode.is2xxSuccessful) {
            response
                .body
                ?: throw RuntimeException("feilet mot saf tilknyttede journalposter, men med 200")
        } else {
            log.error("Feilet mot SAF (${response.statusCode.value()}), body: ${response.body}")
            throw RuntimeException("feilet mot saf med ${response.statusCode.value()}")
        }
    }
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
            brukerId: { id: "$fnr", type:FNR },,
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
