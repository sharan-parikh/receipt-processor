# Receipt Processor

A Java Spring Boot application developed as a solution to the [Fetch Rewards Receipt Processor Challenge](https://github.com/fetch-rewards/receipt-processor-challenge). This service processes digital receipts, calculates reward points based on specific criteria, and provides endpoints to retrieve the computed points.

---

## Features

- **Receipt Submission**: Accepts receipt data via a POST endpoint and returns a unique identifier.
- **Points Calculation**: Implements rules to calculate reward points from receipt details.
- **Points Retrieval**: Provides a GET endpoint to fetch the points awarded for a specific receipt.
- **In-Memory Storage**: Utilizes an in-memory data store for simplicity and ease of testing.
- **Containerized Deployment**: Includes Docker configurations for seamless deployment.

---

## Technologies Used

- **Java 17**
- **Spring Boot 3**
- **Spring Web**
- **Spring Validation**
- **Lombok**
- **Docker & Docker Compose**
- **JUnit 5 & Testcontainers**

---

## Setup & Installation

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Clone the Repository

```bash
git clone https://github.com/sharan-parikh/receipt-processor.git
cd receipt-processor
```

### Build the Application

```bash
./mvnw clean package
```

### Run with Docker Compose

```bash
docker-compose up --build
```

The application will be accessible at `http://localhost:8080`.

---

## API Endpoints

### 1. Process Receipt

- **Endpoint**: `POST /receipts/process`
- **Description**: Submits a receipt for processing and returns a unique ID.
- **Request Body**:

```json
{
  "retailer": "Target",
  "purchaseDate": "2022-01-01",
  "purchaseTime": "13:01",
  "items": [
    {
      "shortDescription": "Mountain Dew 12PK",
      "price": "6.49"
    },
    {
      "shortDescription": "Emils Cheese Pizza",
      "price": "12.25"
    }
  ],
  "total": "35.35"
}
```

- **Response**:

```json
{
  "id": "d6f7e7e4-8c1a-4b9a-9f3c-2b5e4e6f7e8c"
}
```

### 2. Get Points

- **Endpoint**: `GET /receipts/{id}/points`
- **Description**: Retrieves the points awarded for the specified receipt ID.
- **Response**:

```json
{
  "points": 28
}
```

---

## Points Calculation Rules

The points are calculated based on the following rules:

- **Retailer Name**: 1 point for every alphanumeric character.
- **Total Amount**:
    - 50 points if the total is a round dollar amount with no cents.
    - 25 points if the total is a multiple of 0.25.
- **Items**:
    - 5 points for every two items on the receipt.
    - If the trimmed length of the item description is a multiple of 3, multiply the price by 0.2 and round up to the nearest integer. The result is the number of points earned.
- **Purchase Date**: 6 points if the day is odd.
- **Purchase Time**: 10 points if the time is between 2:00 PM and 4:00 PM.

---

## Running Tests

The project includes unit and integration tests.

### Run Tests with Maven

```bash
./mvnw test
```

### Run Tests with Docker Compose

```bash
docker-compose -f docker-compose.test.yml up --build
```

---

##  Docker Configuration

The `docker-compose.yml` file sets up the application and its dependencies.

### Build and Run

```bash
docker-compose up --build
```

### Stop Containers

```bash
docker-compose down
```

---

## 📂 Project Structure

```
receipt-processor/
├── src/
│   ├── main/
│   │   ├── java/com/example/receiptprocessor/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── service/
│   │   │   └── ReceiptProcessorApplication.java
│   └── test/
│       └── java/com/example/receiptprocessor/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 📄 License

This project is licensed under the MIT License.

---

## 🙌 Acknowledgments

- [Fetch Rewards Receipt Processor Challenge](https://github.com/fetch-rewards/receipt-processor-challenge)
