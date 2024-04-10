package no.nav.eux.journalarkivar.webapp.mock

import io.github.oshai.kotlinlogging.KotlinLogging.logger
import jakarta.annotation.PreDestroy
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.springframework.context.annotation.Configuration

@Configuration
class MockWebServerConfiguration(
    val requestBodies: RequestBodies
) {

    val log = logger {}

    private final val server = MockWebServer()

    init {
        server.start(9500)
        server.dispatcher = dispatcher()
    }

    @PreDestroy
    fun shutdown() {
        server.shutdown()
    }

    private final fun dispatcher() = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            log.info { "received ${request.method} ${request.requestUrl} with headers=${request.headers}" }
            val body = request.body.readUtf8()
            requestBodies[request.uriEndsWith] = body
            return mockResponse(request, body)
        }
    }
}
