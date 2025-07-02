package com.receiptprocessor.backend.receipt.repository;

import com.receiptprocessor.backend.receipt.model.ReceiptPoints;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;
import java.util.Optional;

public interface ReceiptPointsRepository extends MongoRepository<ReceiptPoints, UUID> {

  Optional<ReceiptPoints> findByReceiptId(UUID id);

}
