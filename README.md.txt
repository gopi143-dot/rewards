# Rewards API

A Spring Boot application that calculates customer reward points based on their transactions.  
This project demonstrates how to build a RESTful API with Spring Boot, JPA, and Oracle database integration.

---

## 📌 Features
- Manage customers and their transactions.
- Calculate reward points based on transaction amounts:
  - **>$100** → 2 points per dollar over 100, plus 50 points for the $50–$100 range.
  - **$50–$100** → 1 point per dollar over 50.
  - **≤$50** → 0 points.
- Retrieve rewards for a customer over:
  - A custom date range.
  - A configurable number of past months.
  - Default: last 3 months.
- Monthly breakdown of points and total points.
- Transaction details included in the response.

---

## 🛠️ Tech Stack
- **Java 17**
- **Spring Boot 3.2.4**
- **Spring Data JPA**
- **Spring Validation**
- **Springdoc OpenAPI (Swagger UI)**
- **Oracle JDBC Driver**
- **Lombok**

---

## 📂 Project Structure
- `model/` → Entities (`Customer`, `Transaction`)
- `repository/` → JPA repositories
- `dto/` → Data Transfer Objects (`RewardResponseDTO`, `TransactionDTO`)
- `service/` → Service interfaces
- `service/impl/` → Business logic (`RewardServiceImpl`)
- `controller/` → REST endpoints (to be implemented)

---

