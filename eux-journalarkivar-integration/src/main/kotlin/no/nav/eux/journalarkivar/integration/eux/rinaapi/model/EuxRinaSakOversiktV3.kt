package no.nav.eux.journalarkivar.integration.eux.rinaapi.model

data class EuxRinaSakOversiktV3(
    val fnr: String?,
    val sedListe: List<EuxSedOversiktV3>,
    val motparter: List<Motpart>?,
    val navinstitusjon: NavInstitusjon?,
) {
    data class Motpart(
        val formatertNavn: String,
        val motpartLandkode: String,
    )

    data class NavInstitusjon(
        val id: String,
        val navn: String,
    )

    val motpartFormatertNavn
        get(): String = motparter?.firstOrNull()?.formatertNavn ?: "EESSI (ukjent motpart)"

    val navInstitusjonNavn
        get(): String = navinstitusjon?.navn ?: "EESSI (Nav)"
}
