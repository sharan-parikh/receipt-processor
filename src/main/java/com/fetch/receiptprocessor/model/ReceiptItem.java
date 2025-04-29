package com.fetch.receiptprocessor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document("receipt_items")
@Data
public class ReceiptItem {

  @Id
  private UUID id;

  private String shortDescription;

  private BigDecimal price;

  public ReceiptItem() {
    this.id = UUID.randomUUID();
  }

  public ReceiptItem(String shortDescription, BigDecimal price) {
    this();
    this.shortDescription = shortDescription;
    this.price = price;
  }
}
