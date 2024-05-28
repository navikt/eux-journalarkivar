package no.nav.eux.journalarkivar.webapp

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journal.openapi.api.ArkivarprosessApi
import no.nav.eux.journalarkivar.service.FeilregistrerJournalposterService
import no.nav.eux.journalarkivar.service.FerdigstillJournalposterService
import no.nav.eux.journalarkivar.service.clearLocalMdc
import no.nav.eux.journalarkivar.service.mdc
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ArkivarprosessApiImpl(
    val feilregistrerJournalposterService: FeilregistrerJournalposterService,
    val ferdigstillJournalposterService: FerdigstillJournalposterService
) : ArkivarprosessApi {

    val log = logger {}

    @Unprotected
    override fun arkivarprosess(arkivarprosess: String): ResponseEntity<Unit> {
        clearLocalMdc()
        mdc(arkivarprosess = arkivarprosess)
        log.info { "starter arkivarprosess..." }
        when (arkivarprosess) {
            "ferdigstill" -> ferdigstillJournalposterService.ferdigstillJournalposter()
            "feilregistrer" -> feilregistrerJournalposterService.feilregistrerJournalposter()
            else -> {
                log.error { "ukjent arkivarprosess: $arkivarprosess" }
                return ResponseEntity(BAD_REQUEST)
            }
        }
        return ResponseEntity(NO_CONTENT)
    }
}
