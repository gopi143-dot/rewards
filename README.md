#  Rewards API

A Spring Boot application that provides a RESTful API for managing customers, tracking transactions, and calculating reward points.  

---

##  Features
- Manage customers and their transactions.
- Reward points calculation based on transaction amounts.
- Flexible reward retrieval:
- By custom date range.
- By configurable number of past months.
- Default: last 3 months.
- Monthly breakdown of points and total points.
- Transaction details included in responses.
- Interactive API documentation with Swagger UI.

---

##  Tech Stack
- Java 17
- Spring Boot 3.2.4
- Spring Data JPA
- Spring Validation
- Springdoc OpenAPI (Swagger UI)
- Oracle JDBC Driver
- Lombok

---

##  Project Structure
- model/ → Entities (Customer, Transaction)
- repository/ → JPA repositories
- dto/ → Data Transfer Objects (RewardResponseDTO, TransactionDTO)
- service/ → Service interfaces
- service/impl/ → Business logic (RewardServiceImpl)
- controller/ → REST endpoints

---

## 📡 Sample Request
[curl -X 'GET' \
  'http://localhost:8080/api/rewards/customer?customerId=21&months=1&from=2026-01-01&to=2026-03-25&dateRangeValid=true' \
  -H 'accept: */*'](http://localhost:8080/api/rewards/customer/21?from=2026-01-01&to=2026-03-20)

---

## 📊 Sample Response

{
  "customerId": 21,
  "customerName": "Alice",
  "customerEmail": "alice@example.com",
  "monthlyPoints": {
    "2026-03": 404,
    "2026-01": 90,
    "2026-02": 352
  },
  "totalPoints": 846,
  "transactions": [
    {
      "id": 33,
      "amount": 100,
      "transactionDate": "2026-03-01T10:00:00",
      "rewardPoints": 50
    },
    {
      "id": 34,
      "amount": 150.5,
      "transactionDate": "2026-03-15T15:30:00",
      "rewardPoints": 152
    },
    {
      "id": 45,
      "amount": 120,
      "transactionDate": "2026-01-05T09:15:00",
      "rewardPoints": 90
    },
    {
      "id": 46,
      "amount": 250.75,
      "transactionDate": "2026-02-12T14:45:00",
      "rewardPoints": 352
    },
    {
      "id": 47,
      "amount": 100,
      "transactionDate": "2026-03-01T10:00:00",
      "rewardPoints": 50
    },
    {
      "id": 48,
      "amount": 150.5,
      "transactionDate": "2026-03-15T15:30:00",
      "rewardPoints": 152
    }
  ]
}
