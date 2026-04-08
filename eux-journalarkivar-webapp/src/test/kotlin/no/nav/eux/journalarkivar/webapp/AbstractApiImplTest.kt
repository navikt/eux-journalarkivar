package no.nav.eux.journalarkivar.webapp

import io.kotest.assertions.json.shouldEqualSpecifiedJson
import no.nav.eux.journalarkivar.Application
import no.nav.eux.journalarkivar.webapp.mock.RequestBodies
import no.nav.security.mock.oauth2.MockOAuth2Server
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.client.RestTestClient

@SpringBootTest(
    classes = [Application::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@EnableMockOAuth2Server
@AutoConfigureRestTestClient
abstract class AbstractApiImplTest {

    @Autowired
    lateinit var mockOAuth2Server: MockOAuth2Server

    @Autowired
    lateinit var restTestClient: RestTestClient

    @Autowired
    lateinit var requestBodies: RequestBodies

    @BeforeEach
    fun initialiseRestAssuredMockMvcWebApplicationContext() {
        requestBodies.clear()
    }

    infix fun String.requestNumber(number: Int) =
        with(requestBodies[this]) {
            if (this == null)
                null
            else
                this[number]
        }

    infix fun String?.shouldEqual(resource: String) {
        if (this == null)
            error("Resource is null")
        else
            this shouldEqualSpecifiedJson resource.resource
    }

    private val String.resource
        get() = object {}
            .javaClass.getResource(this)!!.readText()

}
