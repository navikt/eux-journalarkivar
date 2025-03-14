package no.nav.eux.journalarkivar.webapp

import io.kotest.assertions.failure
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import no.nav.eux.journalarkivar.Application
import no.nav.eux.journalarkivar.webapp.common.httpEntity
import no.nav.eux.journalarkivar.webapp.common.voidHttpEntity
import no.nav.eux.journalarkivar.webapp.mock.RequestBodies
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    classes = [Application::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@EnableMockOAuth2Server
abstract class AbstractApiImplTest {

    @Autowired
    lateinit var mockOAuth2Server: MockOAuth2Server

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var requestBodies: RequestBodies

    @BeforeEach
    fun initialiseRestAssuredMockMvcWebApplicationContext() {

    }

    val <T> T.httpEntity: HttpEntity<T>
        get() = httpEntity(mockOAuth2Server)

    fun httpEntity() = voidHttpEntity(mockOAuth2Server)

    infix fun String.requestNumber(number: Int) =
        with(requestBodies[this]) {
            if (this == null)
                null
            else
                this[number]
        }

    infix fun String?.shouldEqual(resource: String) {
        if (this == null)
            failure("Resource is null")
        else
            this shouldEqualSpecifiedJson resource.resource
    }

    private val String.resource
        get() = object {}
            .javaClass.getResource(this)!!.readText()

}
