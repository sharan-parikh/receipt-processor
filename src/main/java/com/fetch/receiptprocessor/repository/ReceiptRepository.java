package com.fetch.receiptprocessor.repository;

import com.fetch.receiptprocessor.model.Receipt;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReceiptRepository extends MongoRepository<Receipt, String> {

}
