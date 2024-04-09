package no.nav.eux.journalarkivar.service

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journalarkivar.integration.dokarkiv.client.DokarkivClient
import no.nav.eux.journalarkivar.integration.dokarkiv.model.DokarkivBruker
import no.nav.eux.journalarkivar.integration.dokarkiv.model.DokarkivBrukerType
import no.nav.eux.journalarkivar.integration.dokarkiv.model.DokarkivJournalpostOppdatering
import no.nav.eux.journalarkivar.integration.dokarkiv.model.DokarkivSakOppdatering
import no.nav.eux.journalarkivar.integration.euxjournal.client.JournalClient
import no.nav.eux.journalarkivar.integration.euxnavrinasak.client.EuxNavRinasakClient
import no.nav.eux.journalarkivar.integration.euxnavrinasak.model.Dokument
import no.nav.eux.journalarkivar.integration.euxnavrinasak.model.EuxNavRinasak
import no.nav.eux.journalarkivar.integration.euxnavrinasak.model.EuxSedJournalstatus
import no.nav.eux.journalarkivar.integration.euxnavrinasak.model.EuxSedJournalstatus.Status.JOURNALFOERT
import no.nav.eux.journalarkivar.integration.euxrinaapi.client.EuxRinaApiClient
import no.nav.eux.journalarkivar.integration.saf.client.SafClient
import no.nav.eux.journalarkivar.integration.saf.model.SafJournalpost
import no.nav.eux.journalarkivar.integration.saf.model.SafSak
import org.springframework.stereotype.Service

@Service
class FerdigstillJournalposterService(
    val euxNavRinasakClient: EuxNavRinasakClient,
    val euxRinaApiClient: EuxRinaApiClient,
    val safClient: SafClient,
    val dokarkivClient: DokarkivClient,
    val journalClient: JournalClient,
) {

    val log = logger {}

    fun ferdigstillJournalposter() {
        val euxSedJournalstatuser = euxNavRinasakClient.finn()
        log.info { "${euxSedJournalstatuser.size} dokumenter har ukjent journalføringsstatus" }
        euxSedJournalstatuser.forEach { it.tryFerdigstillJournalpost() }
    }

    fun EuxSedJournalstatus.tryFerdigstillJournalpost() =
        try {
            mdc(rinasakId = rinasakId, sedId = sedId, sedVersjon = sedVersjon)
            ferdigstillJournalpost()
        } catch (e: Exception) {
            log.error(e) { "Feil ved ferdigstilling av journalpost" }
        }

    fun EuxSedJournalstatus.ferdigstillJournalpost() {
        log.info { "henter rinasak $rinasakId" }
        val navRinasak = euxNavRinasakClient.finn(rinasakId)
        val dokument = navRinasak
            .dokumenter
            ?.single { it erSammeSedSom this }
        when {
            dokument != null -> ferdigstillJournalpost(navRinasak, dokument.dokumentInfoId)
            else -> ferdigstillJournalpostUtenNavRinasak()
        }
        this settStatusTil JOURNALFOERT
    }

    fun EuxSedJournalstatus.ferdigstillJournalpost(
        navRinasak: EuxNavRinasak,
        dokumentInfoId: String
    ) {
        mdc(dokumentInfoId = dokumentInfoId)
        log.info { "Ferdigstiller journalpost med dokumentInfoId" }
        val journalpost = safClient
            .firstTilknyttetJournalpostOrNull(dokumentInfoId)
            ?: throw RuntimeException("Fant ikke journalpost for dokumentInfoId")
        mdc(journalpostId = journalpost.journalpostId)
        log.info { "Journalpost har status ${journalpost.journalstatus}" }
        when {
            journalpost.journalstatus.erJournalfoert -> log.info { "Journalpost er allerede journalført" }
            else -> journalpost.ferdigstillJournalpost(navRinasak)
        }
    }

    infix fun EuxSedJournalstatus.settStatusTil(journalstatus: EuxSedJournalstatus.Status) {
        euxNavRinasakClient.put(copy(sedJournalstatus = journalstatus))
        log.info { "Journalstatus satt til $journalstatus" }
    }

    fun SafJournalpost.ferdigstillJournalpost(
        navRinasak: EuxNavRinasak,
    ) {
        val ferdigstiltJournalpost = navRinasak.firstFerdigstiltJournalpost()
        this oppdaterMed ferdigstiltJournalpost
        journalClient.ferdigstill(journalpostId)
        log.info { "Ferdigstilling av journalpost utført" }
    }

    fun EuxSedJournalstatus.ferdigstillJournalpostUtenNavRinasak() {
        val rinasak = euxRinaApiClient.euxRinaSakOversikt(rinasakId)
        safClient
            .dokumentoversiktBrukerRoot(rinasak.fnr!!)
            .data
            .dokumentoversiktBruker
            .journalposter
    }

    fun EuxNavRinasak.firstFerdigstiltJournalpost() =
        dokumenter!!
            .mapNotNull { safClient.firstTilknyttetJournalpostOrNull(it.dokumentInfoId) }
            .first { it.journalstatus.erJournalfoert }

    infix fun SafJournalpost.oppdaterMed(eksisterendeJournalpost: SafJournalpost) {
        log.info { "Oppdaterer $journalpostId med utgangspunkt i ${eksisterendeJournalpost.journalpostId}" }
        val eksisterendeSak = eksisterendeJournalpost.sak!!
        val eksisterendeBruker = eksisterendeJournalpost.bruker!!
        val dokarkivBruker = DokarkivBruker(
            id = eksisterendeBruker.id,
            idType = DokarkivBrukerType.valueOf(eksisterendeBruker.type)
        )
        val dokarkivSak =
            when {
                eksisterendeSak.sakstype == "GENERELL_SAK" -> dokarkivSakOppdateringGenerell
                else -> eksisterendeSak.toDokarkivSakOppdateringFagsak()
            }
        val dokarkivOppdatering = DokarkivJournalpostOppdatering(
            sak = dokarkivSak,
            bruker = dokarkivBruker,
            tema = eksisterendeSak.tema!!
        )
        dokarkivClient.oppdater(journalpostId, dokarkivOppdatering)
        log.info { "Journalpost oppdatert" }
    }
}

val dokarkivSakOppdateringGenerell =
    DokarkivSakOppdatering(
        sakstype = "GENERELL_SAK",
        fagsaksystem = null,
        fagsakId = null
    )

fun SafSak.toDokarkivSakOppdateringFagsak() =
    DokarkivSakOppdatering(
        sakstype = sakstype!!,
        fagsaksystem = fagsaksystem,
        fagsakId = fagsakId
    )

infix fun Dokument.erSammeSedSom(
    journalstatus: EuxSedJournalstatus
) = sedId == journalstatus.sedId && sedVersjon == journalstatus.sedVersjon
