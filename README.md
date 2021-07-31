# QuantumState

How to start the QuantumState application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/QuantumState-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

## USAGE

### Sequences:

#### Create a sequence with increment of 1 and a random start number:

Request: `curl --request POST http://localhost:8080`
Response: `{"id":"55174398258626600"}`

Access sequence by ID:

Request: `curl --request GET http://localhost:8080/?id=55174398258626600`

Response on the 1st request: `{"data":8738223684979975562}`

Response on the 2nd request: `{"data":8738223684979975563}`

Response on the 3rd request: `{"data":8738223684979975564}`
etc

#### Create a limited sequnce with increment of 123 and start number 100 (EXCLUSSIVE!) and a total of 5 requests:
Request:
```
curl --location --request POST http://localhost:8080 \
--header 'Content-Type: application/json' \
--data-raw '{
    "initialValue": 100,
    "step": 123,
    "maxRequests": 5
}'
```

Response: `{"id":"49175190346540100"}`

Access that limited sequence:

Request: `curl --request GET http://localhost:8080/?id=49175190346540100`

1st response: `{"data":223}`

2nd response: `{"data":346}`

3rd response: `{"data":469}`

4th response: `{"data":592}`

5th response: `{"data":715}`

6th+ response: `404 Not Found`


#### Create a sequence with a custom ID of "blahblah"

Request: 
```
curl --location --request POST http://localhost:8080 \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "blahblah",
    "initialValue": 4567890123,
    "step": 1
}'
```

Response: `{"id":"blahblah"}`

Request: `curl --request GET http://localhost:8080/?id=blahblah`

1st response: `{"data":4567890124}`
2nd response: `{"data":4567890125}`
3rd response: `{"data":4567890126}`
etc

#### An attempt to re-create an existing sequence:
Request:
```
curl --request POST http://localhost:8080 \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "blahblah",
    "initialValue": 4567890123,
    "step": 1
}'
```

Response: `409 Conflict`

#### Create a stable data with limited number of requests and limited TTL
Request:
curl --request POST http://localhost:8080 \
--header 'Content-Type: application/json' \
--data-raw '{
    "step": 0,
    "maxRequests": 2,
    "ttlSeconds": 15
}'

Response: {"id": "63176963792998500"}

Request to access the data: `curl --request GET http://localhost:8080/?id=63176963792998500`

1st and 2nd response within 15 seconds: `{"data": 5320243940314473467}`

Either 3+ response or after 15 seconds of initial POST request: `404 Not Found`


## Health Check
---

Default for Dropwizard based service url: `http://localhost:8081/healthcheck`
