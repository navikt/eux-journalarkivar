package no.nav.eux.journalarkivar.webapp

import no.nav.eux.journal.openapi.api.FeilregistrerJournalposterApi
import no.nav.eux.journalarkivar.service.FeilregistrerJournalposterService
import no.nav.eux.journalarkivar.service.mdc
import no.nav.security.token.support.core.api.Unprotected
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class FeilregistrerJournalposterApiImpl(
    val feilregistrerJournalposterService: FeilregistrerJournalposterService
) : FeilregistrerJournalposterApi {

    @Unprotected
    override fun feilregistrerJournalposter(body: Any): ResponseEntity<Unit> {
        mdc(arkivarprosess = "feilregistrerJournalposter")
        feilregistrerJournalposterService.feilregistrerJournalposter()
        return ResponseEntity(NO_CONTENT)
    }
}
