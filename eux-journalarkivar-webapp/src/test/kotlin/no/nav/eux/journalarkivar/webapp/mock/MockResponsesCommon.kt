package no.nav.eux.journalarkivar.webapp.mock

import okhttp3.mockwebserver.MockResponse

fun response200() =
    MockResponse().apply {
        setResponseCode(200)
    }
