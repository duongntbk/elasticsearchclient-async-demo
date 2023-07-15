import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch.async_search.ElasticsearchAsyncSearchClient
import co.elastic.clients.elasticsearch.async_search.GetAsyncSearchRequest
import co.elastic.clients.elasticsearch.async_search.GetAsyncSearchResponse
import co.elastic.clients.elasticsearch.async_search.SubmitRequest
import co.elastic.clients.elasticsearch.async_search.SubmitResponse

class ElasticsearchAsyncSearchClientWrapper(
    login: String,
    password: String,
    fingerprint: String,
): ElasticsearchClientWrapper(login, password, fingerprint) {
    private val client: ElasticsearchAsyncSearchClient = ElasticsearchClient(transport).asyncSearch()

    fun <TDocument> submit(
        request: SubmitRequest,
        tDocumentClass: Class<TDocument>
    ): SubmitResponse<TDocument> = client.submit(request, tDocumentClass)

    fun <TDocument> getResponse(
        request: GetAsyncSearchRequest,
        tDocumentClass: Class<TDocument>
    ): GetAsyncSearchResponse<TDocument> = client.get(request, tDocumentClass)
}
