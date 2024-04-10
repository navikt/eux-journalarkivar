package no.nav.eux.journalarkivar.webapp.mock

import okhttp3.mockwebserver.MockResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.time.Instant

fun tokenResponse() =
    MockResponse().apply {
        setResponseCode(200)
        setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        setBody(tokenResponse)
    }

val tokenResponse = """{
          "token_type": "Bearer",
          "scope": "test",
          "expires_at": "${Instant.now().plusSeconds(3600).epochSecond}",
          "ext_expires_in": "30",
          "expires_in": "30",
          "access_token": "token"
        }"""
