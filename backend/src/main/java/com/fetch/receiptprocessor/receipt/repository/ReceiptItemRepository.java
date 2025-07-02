package com.fetch.receiptprocessor.receipt.repository;


import com.fetch.receiptprocessor.receipt.model.ReceiptItem;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ReceiptItemRepository extends MongoRepository<ReceiptItem, UUID> {
}
