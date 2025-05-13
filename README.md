An API service built with Spring Boot that produces and consumes transaction events from Apache Kafka, 
evaluates them using basic fraud detection rules, and stores relevant data in PostgreSQL.

**Fraud Detection Rules**

Transactions are checked against a set of rules:


| Rule # | Description                                                                                                                      |
|----|----------------------------------------------------------------------------------------------------------------------------------|
| 1  | Flag transactions over **10,000**.                                                                                               |
| 2  | Flag if location is suspicious. This is determined by whether the speed of transaction is faster than 500km/h (speed of a plane) |
| 3  | Flag rapid transactions, 5 transactions in a 5 minute window.                                                                    |

Transactions will be received through the application API, and will be persisted in Postgres as RECEIVED state. Once published to Kafka and consumed, they are updated to PENDING state.
Following fraud checks, the transaction will then go into APPROVED or FLAGGED state depending on whether the transaction is deemed to be fraudulent.

**Getting Started**
1. Clone the repository and build
```bash
   git clone https://github.com/niallmcelvogue/fraud-detection-application.git
   cd fraud-detection-application
   ./mvnw clean package -DskipTests
  ```
2. Run with Docker/Podman Compose
```bash
  podman-compose up --build
  docker-compose up --build
```

**Example Payload (JSON)**
```json
{
  "amount": 1200.50,
  "timestamp": "2025-05-13T12:00:00Z",
  "userId": "user123",
  "merchantId": "merchant456",
  "latitude": 53.349805,
  "longitude": -6.26031
}
```

**Endpoints**
```
POST http://localhost:8080/api/transactions
Run along with JSON body as above
```

```
GET http://localhost:8080/api/transactions
Return all transactions
```

## ✅ TODO List

- [ ] Define user/merchant controller, entity and repository
- [ ] Add approve/fraudulent logic for when a transaction is marked as flagged
- [ ] Add machine learning feature, to detect anomalies in user transactions

