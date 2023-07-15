import co.elastic.clients.elasticsearch._types.Time
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import co.elastic.clients.elasticsearch.async_search.GetAsyncSearchRequest
import co.elastic.clients.elasticsearch.async_search.SubmitRequest
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.search.HitsMetadata
import co.elastic.clients.json.JsonData
import com.google.common.base.Stopwatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.FileInputStream
import java.util.Properties
import java.util.concurrent.TimeUnit

suspend fun main(args: Array<String>) {
    val (login, password, fingerprint) = loadConfigKeys()

    ElasticsearchAsyncClientWrapper(login, password, fingerprint, 2000).use { client ->
        testSearchAsync(client)
    }

    ElasticsearchAsyncSearchClientWrapper(login, password, fingerprint).use {client ->
        testAsyncSearch(client)
    }
}

private suspend fun testSearchAsync(client: ElasticsearchAsyncClientWrapper) = coroutineScope {
    val request1 = buildQueryByClass()
    val request2 = buildQueryByClass()

    val stopWatch = Stopwatch.createStarted()

    val deferredResponse1 = async(Dispatchers.IO) {
        client.search(request1, Footballer::class.java)
    }
    val deferredResponse2 = async(Dispatchers.IO) {
        client.search(request2, Footballer::class.java)
    }

    awaitAll(deferredResponse1, deferredResponse2)
    val response1 = deferredResponse1.await()
    val response2 = deferredResponse2.await()

    val processTime = stopWatch.elapsed(TimeUnit.MILLISECONDS)

    println("Hits returned: ${response1.hits().total()?.value() ?: 0}")
    println("Hits returned: ${response2.hits().total()?.value() ?: 0}")
    println("Processing time: $processTime")
}

private fun testAsyncSearch(client: ElasticsearchAsyncSearchClientWrapper) {
    val submitRequest = SubmitRequest.Builder()
        .index("footballer")
        .query(buildQueryByClass().query())
        .keepOnCompletion(true)
        .keepAlive(Time.of { t -> t.time("30s") })
        .build()

    val submitResponse = client.submit(submitRequest, Footballer::class.java)
    println("==========================================================")
    println("Id: ${submitResponse.id()}, IsPartial: ${submitResponse.isPartial}, IsRunning: ${submitResponse.isRunning}")

    Thread.sleep(2000)

    val responseRequest = GetAsyncSearchRequest.Builder().id(submitResponse.id()).build()
    val response = client.getResponse(responseRequest, Footballer::class.java)

    if (!response.isRunning) {
        printResults(response.response().hits())
    }
}

private fun buildQueryByClass(): SearchRequest {
    val positionTerm1 = TermQuery.Builder().field("position").value("rw").boost(2F).build()._toQuery()
    val positionTerm2 = TermQuery.Builder().field("position").value("lw").build()._toQuery()
    val positionQuery = BoolQuery.Builder()
        .should(positionTerm1, positionTerm2)
        .build()
        ._toQuery()

    val ageRange = RangeQuery.Builder().field("age").lte(JsonData.of(23)).build()._toQuery()
    val salaryRange = RangeQuery.Builder().field("salary").lte(JsonData.of(200)).boost(2F).build()._toQuery()
    val ageAndSalaryQuery = BoolQuery.Builder()
        .should(ageRange, salaryRange)
        .build()
        ._toQuery()

    val query = BoolQuery.Builder()
        .must(positionQuery, ageAndSalaryQuery)
        .build()
        ._toQuery()

    return SearchRequest.Builder()
        .index("footballer")
        .query(query)
        .build()
}

private fun printResults(hits: HitsMetadata<Footballer>) {
    println("==========================================================")
    println("Hits: ${hits.total()?.value()}")
    for (hit in hits.hits()) {
        println("Name: ${hit.source()?.name}, Score: ${hit.score()}")
    }
}

private fun loadConfigKeys(): Triple<String, String, String> {
    val properties = Properties()

    val localFile = "src/main/resources/application-env-local.yml"
    val file = if (java.io.File(localFile).exists()) localFile else "src/main/resources/application.yml"

    FileInputStream(file).use { inputStream ->
        properties.load(inputStream)
    }

    val login = properties.getProperty("login")
    val password = properties.getProperty("password")
    val fingerprint = properties.getProperty("fingerprint")
    return Triple(login, password, fingerprint)
}
