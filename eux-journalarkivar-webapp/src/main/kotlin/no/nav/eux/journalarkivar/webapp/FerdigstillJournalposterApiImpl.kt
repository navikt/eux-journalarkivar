package no.nav.eux.journalarkivar.webapp

import no.nav.eux.journal.openapi.api.FerdigstillJournalposterApi
import no.nav.eux.journalarkivar.service.JournalService
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class FerdigstillJournalposterApiImpl(
    val journalService: JournalService
) : FerdigstillJournalposterApi {

    @Unprotected
    override fun ferdigstillJournalposter(body: Any): ResponseEntity<Unit> {
        journalService.ferdigstillJournalposter()
        return ResponseEntity(NO_CONTENT)
    }
}
