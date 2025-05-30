# Metric service
This project was created to deepen my understanding of

### Helidon SE 4
    For building Java microservices
### Liquibase
    For database version control
### OpenAPI3
    For documenting and designing RESTful API
### Hexagonal Architecture (Ports and adapters)
    Pattern for creating maintainable and flexible software systems

## 🛠️ Installation

Clone the repository:
https://github.com/Frlundsten/metric-service.git
---

The service has an implemented feature where a mail will be sent if a trendcheck fails.
The dependency used for this is:
```yaml 
<dependency>
    <groupId>org.simplejavamail</groupId>
    <artifactId>simple-java-mail</artifactId>
    <version>8.12.2</version>
</dependency>
```
To use the mail functionality you would need to register at an email delivery platform like [Mailtrap](https://mailtrap.io/).
If you do not provide any mail env values it will default to the applications default values and an exception will be thrown if the ```sendAlert()``` method is called. 

```yaml
metric-service:
  build:
    context: .
  environment:
    db.connection.url: jdbc:postgresql://postgres-container:5432/helidon
    db.connection.username: user
    db.connection.password: password
#    mail.host:
#    mail.port:
#    mail.username:
#    mail.password:
#    mail.recipient:
  ports:
    - "8080:8080"
  depends_on:
    liquibase:
      condition: service_completed_successfully
```
---

> ⚠️ **Java Version Requirement**  
> This project requires **Java 24** (a preview version beyond Java 21) to build and run properly.  
>  
> The `pom.xml` specifies the compiler release as `24` and enables preview features with `--enable-preview`.  
>  
> Please ensure you have a JDK that supports Java 24 preview features installed and configured before building or running this project.  
>  
> If you want to run this on Java 21 or earlier, you will need to update the Maven configuration accordingly and remove the preview flags.

---


## 🏃‍♂️ Run the application
Start all containers (Postgres, Liquibase, and the application) by running:

```bash
docker compose up --build
```
---
## 📡 API Endpoints

All requests **require** the HTTP header:  
```Repository-Id: <your-repository-id>```
---

### POST `/metrics`
- **Description:** Submit metrics data.
- **Headers:**  
  - `Repository-Id` (required)

```bash
curl -X POST http://localhost:8080/metrics \
  -H "Repository-Id: repo-1" \
  -H "Content-Type: application/json" \
  -d '{
    "state": {
      "isStdOutTTY": true,
      "isStdErrTTY": true,
      "testRunDurationMs": 10705.3931
    },
    "metrics": {
      "http_req_duration": {
        "type": "trend",
        "contains": "time",
        "values": {
          "avg": 0.05408765957446809,
          "min": 0,
          "med": 0,
          "max": 1.0048,
          "p(90)": 0,
          "p(95)": 0.52686
        }
      },
      "data_sent": {
        "type": "counter",
        "contains": "data",
        "values": {
          "count": 27495,
          "rate": 2568.3317
        }
      }
    },
    "root_group": {
      "path": "",
      "id": "d21d6bd94a00b504e9200998ecf2447e",
      "groups": [],
      "checks": [],
      "name": ""
    },
    "options": {
      "summaryTrendStats": ["avg", "min", "med", "max", "p(90)", "p(95)"],
      "summaryTimeUnit": "",
      "noColor": false
    }
  }'

```

 ---
**Note on datetime format:**

The `from` and `to` query parameters expect datetimes in [ISO 8601](https://en.wikipedia.org/wiki/ISO_8601) format, e.g., `YYYY-MM-DDTHH:mm:ssZ`.

Since URLs cannot contain raw `:` characters, you **must URL-encode** the colon as `%3A`.

 ---
 
### GET  `/metrics`
- **Description:** Retrieve metrics filtered by datetime.
- **Headers:**  
  - `Repository-Id` (required)
- **Query Parameters:**
  - `from` (required)
  - `to` (required)
```bash
curl -X GET "http://localhost:8080/metrics?from=2017-01-01T00%3A00%3A00Z&to=2027-01-01T00%3A00%3A00Z" \
  -H "Repository-Id: repo-1" \
  -H "Accept: application/json"
```

---

### GET  `/metrics`
- **Description:** Retrieve metric by name filtered by datetime.
- **Headers:**  
  - `Repository-Id` (required)
- **Query Parameters:**
  - `name`(the name of the specific metric to fetch)
  - `from` (required)
  - `to` (required)
```bash
curl -X GET "http://localhost:8080/metrics?name=http_req_receiving&from=2017-01-02T00%3A00%3A00Z&to=2027-01-02T00%3A00%3A00Z" \
  -H "Repository-Id: repo-1" \
  -H "Accept: application/json"
```

---

### GET  `/metrics/recent`
- **Description:** Retrieves metrics from the last 30 days for the specified repository. The data is served from a materialized view for optimized performance.
- **Headers:**  
  - `Repository-Id` (required)
```bash
curl -X GET http://localhost:8080/metrics/recent \
  -H "Repository-Id: repo-1" \
  -H "Accept: application/json"
```

