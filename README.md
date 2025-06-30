# Receipt Processor

A Java Spring Boot application developed as a solution to the [Fetch Rewards Receipt Processor Challenge](https://github.com/fetch-rewards/receipt-processor-challenge). This service processes digital receipts, calculates reward points based on specific criteria, and provides endpoints to retrieve the computed points.

## Project Structure

This project follows a monorepo structure:

```
receipt-processor/
├── backend/          # Spring Boot application
│   ├── src/         # Java source code
│   ├── pom.xml      # Maven configuration
│   ├── mvnw*        # Maven wrapper
│   └── Dockerfile   # Backend containerization
├── frontend/         # Placeholder for frontend application
└── compose.yaml      # Docker Compose configuration
```

The backend contains the complete Spring Boot application with all its dependencies and can be built and run independently from the `/backend` directory.

---

## Features

- **Receipt Submission**: Accepts receipt data via a POST endpoint and returns a unique identifier.
- **Points Calculation**: Implements rules to calculate reward points from receipt details.
- **Points Retrieval**: Provides a GET endpoint to fetch the points awarded for a specific receipt.
- **In-Memory Storage**: Utilizes an in-memory data store for simplicity and ease of testing.
- **Containerized Deployment**: Includes Docker configurations for seamless deployment.
- **API Documentation**: Interactive Swagger UI for API exploration (development only).

---

## Environment Profiles

The application supports different environment profiles for configuration management:

### Development Profile (`dev`)
- **Swagger UI**: Enabled at `http://localhost:8080/swagger-ui.html`
- **API Docs**: Available at `http://localhost:8080/api-docs`
- **Full API Documentation**: Interactive documentation for testing and development

### Production Profile (`prod`)
- **Swagger UI**: Disabled for security
- **API Docs**: Disabled for security
- **Optimized**: No documentation endpoints exposed

### Running with Profiles

```bash
# Development mode (default)
cd backend
./mvnw spring-boot:run

# Development mode (explicit)
cd backend
./mvnw spring-boot:run -Dspring.profiles.active=dev

# Production mode
cd backend
./mvnw spring-boot:run -Dspring.profiles.active=prod

# Docker with specific profile
docker-compose up --build -e SPRING_PROFILES_ACTIVE=prod
```

---

## Design Decisions

### Clean Architecture
The application is structured following standard layering:
- **Controller Layer**: Handles HTTP requests and delegates processing.
- **Service Layer**: Contains business logic for receipt processing and points computation.
- **Model/DTO Layer**: Separates domain and transport concerns using `@Document` and DTO objects.
- **Repository Layer**: Data layer that persists the data.

### Validation
- Payload validation is handled using `@Valid` and standard JSR-380 annotations (`@NotNull`, `@Pattern`, etc.) in the DTOs.
- Global exception handling is provided via `@ControllerAdvice` for maintainable error responses.

### Maintainability
- Follows **SOLID principles** and separation of concerns, making it easy to test and extend.
- Adding new rules for point calculation can be done by introducing new methods in the service layer without affecting controllers.
- `ReceiptItem` and `ReceiptDTO` are cleanly modeled, making serialization/deserialization robust.
- Dockerized environment allows consistent setup across different systems.

### Extensibility
- Can easily be extended to persist data in a real database.
- Future enhancements like authentication, rate limiting, or analytics can be plugged in with minimal changes due to layered design.

### PointsService and Rule Calculators

The core logic for calculating points is encapsulated in a modular and extensible service called 
`PointsService`. This service delegates the calculation to a list of independently defined rules 
located in the `com.fetch.receiptprocessor.calculators` package. Each rule implements a `PointCalculationRule` interface, 
promoting **single responsibility** and **open/closed principles**.

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

### Testing the Application

If you want to run the tests, run the following command:
```bash
cd backend
./mvnw test
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

## License

This project is licensed under the MIT License.

---

## Acknowledgments

- [Fetch Rewards Receipt Processor Challenge](https://github.com/fetch-rewards/receipt-processor-challenge)
