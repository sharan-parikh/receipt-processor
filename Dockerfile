# Build stage
FROM eclipse-temurin:17-jdk-jammy as builder
WORKDIR /receipt-processor-service

# Copy build files and download dependencies (cached unless pom.xml changes)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline

# Copy source code and build (cached unless src changes)
COPY src src
RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /receipt-processor-service

# Copy only the built JAR
COPY --from=builder /receipt-processor-service/target/*.jar receipt-processor-service.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "receipt-processor-service.jar"]