package com.receiptprocessor.backend.receipt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceiptItem {

  @Field("short_description")
  private String shortDescription;

  @Field("price")
  private BigDecimal price;

  public ReceiptItem(String shortDescription, BigDecimal price) {
    this.shortDescription = shortDescription;
    this.price = price;
  }
}
