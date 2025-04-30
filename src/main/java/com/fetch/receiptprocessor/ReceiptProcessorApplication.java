package com.fetch.receiptprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class ReceiptProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReceiptProcessorApplication.class, args);
	}

}
