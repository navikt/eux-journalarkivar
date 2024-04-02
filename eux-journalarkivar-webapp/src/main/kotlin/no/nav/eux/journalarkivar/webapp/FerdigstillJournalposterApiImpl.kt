package no.nav.eux.journalarkivar.webapp

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journal.openapi.api.FerdigstillJournalposterApi
import no.nav.security.token.support.core.api.Protected
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class FerdigstillJournalposterApiImpl : FerdigstillJournalposterApi {
    val log = logger {}

    @Unprotected
    override fun ferdigstillJournalposter(body: Any): ResponseEntity<Unit> {
        log.info { "tjohei!" }
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
