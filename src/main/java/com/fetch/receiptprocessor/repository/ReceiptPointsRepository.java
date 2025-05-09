package com.fetch.receiptprocessor.repository;

import com.fetch.receiptprocessor.model.ReceiptPoints;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;
import java.util.Optional;

public interface ReceiptPointsRepository extends MongoRepository<ReceiptPoints, UUID> {

  Optional<ReceiptPoints> findByReceiptId(UUID id);

}
