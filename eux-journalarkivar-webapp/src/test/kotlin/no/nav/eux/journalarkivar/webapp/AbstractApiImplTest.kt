package no.nav.eux.journalarkivar.webapp

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.assertions.json.shouldEqualSpecifiedJson
import io.kotest.matchers.shouldBe
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

    private val objectMapper = ObjectMapper()

    @Autowired
    lateinit var mockOAuth2Server: MockOAuth2Server

    @Autowired
    lateinit var restTestClient: RestTestClient

    @Autowired
    lateinit var requestBodies: RequestBodies

    @BeforeEach
    fun resetTestState() {
        requestBodies.clear()
    }

    val String.requests: List<String>
        get() = requestBodies[this] ?: emptyList()

    infix fun String.requestNumber(number: Int) = requests.getOrNull(number)

    infix fun String?.shouldEqual(resource: String) {
        if (this == null)
            error("Resource is null")
        else
            this shouldEqualSpecifiedJson resource.resource
    }

    infix fun List<String>.shouldEqual(resource: String) =
        joinToString(prefix = "[", postfix = "]") shouldEqualSpecifiedJson resource.resource

    infix fun String?.shouldEqualGraphQlQuery(resource: String) {
        if (this == null)
            error("GraphQL request is null")
        else
            graphQlQuery.normalizedWhitespace shouldBe resource.resource.graphQlQuery.normalizedWhitespace
    }

    private val String.graphQlQuery
        get() = objectMapper
            .readTree(this)
            .findValue("query")
            ?.asText()
            ?: error("Fant ikke GraphQL query: $this")

    private val String.normalizedWhitespace
        get() = trim().replace(Regex("\\s+"), " ")

    private val String.resource
        get() = object {}
            .javaClass.getResource(this)!!.readText()

}
