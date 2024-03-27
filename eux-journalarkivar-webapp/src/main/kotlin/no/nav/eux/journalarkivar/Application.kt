package no.nav.eux.journalarkivar

import no.nav.security.token.support.spring.api.EnableJwtTokenValidation
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableJwtTokenValidation(
    ignore = [
        "org.springframework",
        "org.springdoc"
    ]
)
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
