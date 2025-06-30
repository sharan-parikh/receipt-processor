package com.fetch.receiptprocessor.repository;

import com.fetch.receiptprocessor.model.Receipt;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ReceiptRepository extends MongoRepository<Receipt, UUID> {

}
