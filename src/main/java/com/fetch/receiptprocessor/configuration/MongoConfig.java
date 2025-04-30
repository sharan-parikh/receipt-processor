package com.fetch.receiptprocessor.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.fetch.receiptprocessor.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

  @Value("${spring.data.mongodb.uri}")
  private String mongoDbConnectionUri;

  @Value("${spring.data.mongodb.database}")
  private String dbName;

  @Bean
  MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
    return new MongoTransactionManager(dbFactory);
  }

  @Override
  protected String getDatabaseName() {
    return dbName;
  }

  @Override
  public MongoClient mongoClient() {
    final ConnectionString connectionString = new ConnectionString(mongoDbConnectionUri);
    final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .build();
    return MongoClients.create(mongoClientSettings);
  }
}
