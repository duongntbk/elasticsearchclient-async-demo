import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.SearchResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await

class ElasticsearchAsyncClientWrapper(
    login: String,
    password: String,
    fingerprint: String,
    private val delayInMs: Long = 0,
) : ElasticsearchClientWrapper(login, password, fingerprint) {
    private val client: ElasticsearchAsyncClient = ElasticsearchAsyncClient(transport)

    suspend fun <TDocument> search(
        request: SearchRequest,
        tDocumentClass: Class<TDocument>
    ): SearchResponse<TDocument> {
        if (delayInMs > 0) {
            delay(delayInMs)
        }
        return client.search(request, tDocumentClass).await()
    }
}
