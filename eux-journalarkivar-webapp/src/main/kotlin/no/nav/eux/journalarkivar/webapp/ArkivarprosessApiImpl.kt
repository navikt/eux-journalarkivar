package no.nav.eux.journalarkivar.webapp

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journal.openapi.api.ArkivarprosessApi
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ArkivarprosessApiImpl : ArkivarprosessApi {

    val log = logger {}

    @Unprotected
    override fun arkivarprosess(arkivarprosess: String): ResponseEntity<Unit> {
        log.info { "Arkivarprosess: $arkivarprosess" }
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}