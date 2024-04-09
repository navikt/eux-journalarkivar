package no.nav.eux.journalarkivar.webapp

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import no.nav.eux.journal.openapi.api.FerdigstillJournalposterApi
import no.nav.eux.journalarkivar.service.FerdigstillJournalposterService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class FerdigstillJournalposterApiImpl(
    val ferdigstillJournalposterService: FerdigstillJournalposterService
) : FerdigstillJournalposterApi {
    val log = logger {}

    @Unprotected
    override fun ferdigstillJournalposter(body: Any): ResponseEntity<Unit> {
        ferdigstillJournalposterService.ferdigstillJournalposter()
        return ResponseEntity(NO_CONTENT)
    }
}
