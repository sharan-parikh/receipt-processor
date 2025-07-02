package com.fetch.receiptprocessor.receipt.repository;

import com.fetch.receiptprocessor.receipt.model.Receipt;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ReceiptRepository extends MongoRepository<Receipt, UUID> {

}
