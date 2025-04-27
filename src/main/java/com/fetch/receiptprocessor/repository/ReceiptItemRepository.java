package com.fetch.receiptprocessor.repository;


import com.fetch.receiptprocessor.model.ReceiptItem;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReceiptItemRepository extends MongoRepository<ReceiptItem, String> {
}
