package com.receiptprocessor.backend.receipt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

import lombok.Data;

@Data
@Document("receipt_points")
public class ReceiptPoints {

  @Id
  private UUID id;

  private UUID receiptId;

  private int points;

  public ReceiptPoints() {
    this.id = UUID.randomUUID();
  }

}
