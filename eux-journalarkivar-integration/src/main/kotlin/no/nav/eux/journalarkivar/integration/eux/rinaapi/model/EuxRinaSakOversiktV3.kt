package no.nav.eux.journalarkivar.integration.eux.rinaapi.model

data class EuxRinaSakOversiktV3(
    val fnr: String?,
    val sedListe: List<EuxSedOversiktV3>
)
