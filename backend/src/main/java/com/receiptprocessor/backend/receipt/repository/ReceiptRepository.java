package com.receiptprocessor.backend.receipt.repository;

import com.receiptprocessor.backend.receipt.model.Receipt;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ReceiptRepository extends MongoRepository<Receipt, UUID> {

}
