# Build stage
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /receipt-processor-service

# Cache Maven dependencies
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Build application
COPY src src
RUN ./mvnw clean package -DskipTests

# Test stage (optional)
FROM builder as tester
RUN ./mvnw test

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /receipt-processor-service
COPY --from=builder /receipt-processor-service/target/*.jar receipt-processor-service.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "receipt-processor-service.jar"]