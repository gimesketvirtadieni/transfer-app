# transfer-app

Building Money Transfer Application
-------------------------------
Following command should be used from ‘test’ directory:  
`mvn clean package`

Starting Transfer Application
-----------------------------
Following command should be used from ‘test/transfer-app’ directory:  
`java -jar target/transfer-app-0.0.1-SNAPSHOT.jar server transfer-app.yml`

Inspecting Database While Application Is Running
------------------------------------------------
Following command should be used from ‘test/transfer-app’ directory:  
`java -cp h2-1.4.199.jar org.h2.tools.Server`

This command will start a process able to communicate with in-memory DB used by the application.
Once started, it will open a browser window where following connection should be used:  
Driver Class: org.h2.Driver  
JDBC URL: jdbc:h2:tcp://localhost/mem:transfer-db  
User Name: sa  
Password: sa  

Application RESTful Endpoints
-----------------------------
Once started, application exposes following endpoints:
- GET     /api/v1/accounts – returns list of accounts
- GET     /api/v1/accounts{id} – returns details about one account by provided ID
- GET     /api/v1/currencies – returns list of currencies
- GET     /api/v1/currencies{id} – returns details about one currency by provided ID
- GET     /api/v1/transactions – returns list of transactions
- GET     /api/v1/transactions{id} – returns details about one transaction by provided ID
- POST    /api/v1/transfers – processes money transfer operation

They can be used as following:  
`curl -H "Content-Type: application/json" http://localhost:8080/api/v1/transactions`  
`curl -d @sample1.json -H "Content-Type: application/json" -X POST http://localhost:8080/api/v1/transfers`

Application Design
------------------
The application heavily relies on Dropwizard framework, which is a collection of various tools for creating micro services.
Underneath it uses Jetty Servlet container, which uses a thread pool for incoming request; a dedicated thread is borrowed from the pool per every HTTP request.
In case HTTP request processing requires DB access, a DB connection is obtained from a pool.
Safe concurrency is ensured by DB means – records are exclusively locked while performing read-compare-write operations.
There is no ‘application’ related shared state used by the application, except for the actual DB, so no need to synchronize request processing by using Java means.

Random Notes / Reflection
-------------------------
- The design was chosen mainly because of rapid development possibility.
- There are several Unit and Integration test provided to demonstrate the usage, however actual coverage is very low (full coverage would require substantial amount of time).
- Presented design approach is suitable for small and medium size deployments, however it suffers from numerous drawbacks (see below).
- Performance and scalability are limited by DB as all requests hit DB and there is no caching (expect for DB internal caching).
- Each request uses its one thread, hence this design suffers from C10K problem.
- A proper design could rely on storing all the accounts in memory and write to disk only committed transactions.
- For highest possible performance, event sourcing should be considered together with ‘append-only’ DB instead of transactional SQL DB.
