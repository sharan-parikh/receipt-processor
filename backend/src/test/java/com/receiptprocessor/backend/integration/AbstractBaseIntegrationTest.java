package com.receiptprocessor.backend.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public class AbstractBaseIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
          .withExposedPorts(27017);

  @DynamicPropertySource
  static void containersProperties(DynamicPropertyRegistry registry) {
    mongoDBContainer.start();
    registry.add("spring.data.mongodb.host", mongoDBContainer::getHost);
    registry.add("spring.data.mongodb.port", mongoDBContainer::getFirstMappedPort);
  }

}
