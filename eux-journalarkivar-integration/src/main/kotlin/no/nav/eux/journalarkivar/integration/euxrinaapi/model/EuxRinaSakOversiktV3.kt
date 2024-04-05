package no.nav.eux.journalarkivar.integration.euxrinaapi.model

data class EuxRinaSakOversiktV3(
    val fnr: String?,
    val sedListe: List<EuxSedOversiktV3>
)
