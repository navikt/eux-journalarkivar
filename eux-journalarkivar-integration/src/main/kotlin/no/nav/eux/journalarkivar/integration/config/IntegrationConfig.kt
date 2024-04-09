package no.nav.eux.journalarkivar.integration.config

import no.nav.security.token.support.client.core.ClientProperties
import no.nav.security.token.support.client.core.oauth2.OAuth2AccessTokenService
import no.nav.security.token.support.client.spring.ClientConfigurationProperties
import no.nav.security.token.support.client.spring.oauth2.EnableOAuth2Client
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate

@EnableOAuth2Client(cacheEnabled = true)
@Configuration
class IntegrationConfig {

    @Bean
    fun euxOppgaveRestTemplate(components: RestTemplateComponents) =
        components prefixBy "eux-oppgave"

    @Bean
    fun euxJournalRestTemplate(components: RestTemplateComponents) =
        components prefixBy "eux-journal"

    @Bean
    fun euxNavRinasakRestTemplate(components: RestTemplateComponents) =
        components prefixBy "eux-nav-rinasak"

    @Bean
    fun euxRinaApiRestTemplate(components: RestTemplateComponents) =
        components prefixBy "eux-rina-api"

    @Bean
    fun safRestTemplate(components: RestTemplateComponents) =
        components prefixBy "saf"

    @Bean
    fun dokarkivRestTemplate(components: RestTemplateComponents) =
        components prefixBy "dokarkiv"

    @Bean
    fun restTemplateComponents(
        restTemplateBuilder: RestTemplateBuilder,
        clientConfigurationProperties: ClientConfigurationProperties,
        oAuth2AccessTokenService: OAuth2AccessTokenService
    ) = RestTemplateComponents(
        restTemplateBuilder = restTemplateBuilder,
        clientConfigurationProperties = clientConfigurationProperties,
        oAuth2AccessTokenService = oAuth2AccessTokenService
    )

    infix fun RestTemplateComponents.prefixBy(appName: String): RestTemplate {
        val clientProperties: ClientProperties = clientConfigurationProperties
            .registration["$appName-credentials"]
            ?: throw RuntimeException("could not find oauth2 client config for $appName-credentials")
        return restTemplateBuilder
            .additionalInterceptors(bearerTokenInterceptor(clientProperties, oAuth2AccessTokenService))
            .build()
    }

    fun bearerTokenInterceptor(
        clientProperties: ClientProperties,
        oAuth2AccessTokenService: OAuth2AccessTokenService
    ) = ClientHttpRequestInterceptor { request: HttpRequest, body: ByteArray, execution: ClientHttpRequestExecution ->
        val response = oAuth2AccessTokenService.getAccessToken(clientProperties)
        request.headers.setBearerAuth(response.accessToken!!)
        execution.execute(request, body)
    }
}

fun RestTemplate.restClient() = RestClient.create(this)
fun RestTemplate.post() = restClient().post()
fun RestTemplate.put() = restClient().put()
fun RestTemplate.get() = restClient().get()
fun RestTemplate.patch() = restClient().patch()

data class RestTemplateComponents(
    val restTemplateBuilder: RestTemplateBuilder,
    val clientConfigurationProperties: ClientConfigurationProperties,
    val oAuth2AccessTokenService: OAuth2AccessTokenService
)
