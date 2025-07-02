package com.receiptprocessor.backend.receipt.repository;


import com.receiptprocessor.backend.receipt.model.ReceiptItem;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ReceiptItemRepository extends MongoRepository<ReceiptItem, UUID> {
}
