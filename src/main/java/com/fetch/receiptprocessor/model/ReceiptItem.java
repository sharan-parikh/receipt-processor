package com.fetch.receiptprocessor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("receipt_items")
@Data
@NoArgsConstructor
public class ReceiptItem {

  @Id
  private String id;

  private String shortDescription;

  private BigDecimal price;

  public ReceiptItem(String shortDescription, BigDecimal price) {
    this.shortDescription = shortDescription;
    this.price = price;
  }
}
