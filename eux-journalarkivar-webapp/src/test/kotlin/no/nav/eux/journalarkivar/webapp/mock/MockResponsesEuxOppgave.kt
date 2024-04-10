package no.nav.eux.journalarkivar.webapp.mock

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.mockwebserver.MockResponse

fun tildelEnhetsnrResponse(body: String): MockResponse {
    val journalpostId = ObjectMapper()
        .readTree(body)
        .findValue("journalpostId")
        .asText()
    val tildeltEnhetsnr = ObjectMapper()
        .readTree(body)
        .findValue("tildeltEnhetsnr")
        .asText()
    println("Det ble utført tildeling av enhetsnr $tildeltEnhetsnr for journalpostId $journalpostId")
    return if (listOf("453802638", "453802639").contains(journalpostId) && tildeltEnhetsnr == "2950") {
        MockResponse().apply {
            setResponseCode(200)
        }
    } else {
        MockResponse().apply {
            setResponseCode(500)
        }
    }
}
