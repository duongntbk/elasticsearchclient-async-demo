This is a sample app for my blog post at the link below.

[https://duongnt.com/elasticsearch-async-request-kotlin](https://duongnt.com/elasticsearch-async-request-kotlin)

# Usage

## Prerequisites

You need an Elasticsearch cluster to run the code in this repository. It can either be a Docker image, a local server, or a remote server. Please update the fingerprint, login, and password [here](/src/main/resources/application.yml) with the correct credentials.

Also, please follow [this](https://duongnt.com/query-boosting-elasticsearch) blog post to populate the cluster.

## Run the sample code

Run the application using your IDE of choice. You should see the following results in console.
```
Hits returned: 5
Hits returned: 5
Processing time: <a number around 2>
==========================================================
Id: <a random Id>, IsPartial: false, IsRunning: false
==========================================================
Hits: 5
Name: Bukayo Saka, Score: 4.787636
Name: Antony, Score: 4.145132
Name: Mahrez, Score: 3.7876358
Name: Sancho, Score: 2.1451323
Name: Vinicius Junior, Score: 2.1451323
```

# License

MIT License

https://opensource.org/licenses/MIT
