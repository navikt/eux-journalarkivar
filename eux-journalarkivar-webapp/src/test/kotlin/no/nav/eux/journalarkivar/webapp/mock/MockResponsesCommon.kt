package no.nav.eux.journalarkivar.webapp.mock

import okhttp3.mockwebserver.MockResponse

fun response200() =
    MockResponse().apply {
        setResponseCode(200)
    }

fun response404() =
    MockResponse().apply {
        setResponseCode(404)
    }
