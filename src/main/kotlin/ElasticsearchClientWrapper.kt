import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.ElasticsearchTransport
import co.elastic.clients.transport.TransportUtils
import co.elastic.clients.transport.rest_client.RestClientTransport
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient
import java.io.Closeable

abstract class ElasticsearchClientWrapper(
    login: String,
    password: String,
    fingerprint: String,
) : Closeable {
    protected val transport: ElasticsearchTransport

    init {
        val sslContext = TransportUtils.sslContextFromCaFingerprint(fingerprint)

        val credsProv = BasicCredentialsProvider()
        credsProv.setCredentials(AuthScope.ANY, UsernamePasswordCredentials(login, password))

        val restClient = RestClient
            .builder(HttpHost("localhost", 9200, "https"))
            .setHttpClientConfigCallback { hc -> hc
                .setSSLContext(sslContext)
                .setSSLHostnameVerifier { _, _ -> true } // DANGER!!
                .setDefaultCredentialsProvider(credsProv)
            }
            .build()

        transport = RestClientTransport(
            restClient, JacksonJsonpMapper()
        )
    }

    override fun close() {
        transport.close()
    }
}
