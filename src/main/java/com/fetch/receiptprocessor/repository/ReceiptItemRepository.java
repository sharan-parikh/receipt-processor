package com.fetch.receiptprocessor.repository;


import com.fetch.receiptprocessor.model.ReceiptItem;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ReceiptItemRepository extends MongoRepository<ReceiptItem, UUID> {
}
