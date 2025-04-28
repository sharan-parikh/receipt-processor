package com.fetch.receiptprocessor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Document("receipts")
@Data
@NoArgsConstructor
public class Receipt {

  @Id
  private UUID id;

  private String retailer;

  private LocalDate purchaseDate;

  private LocalTime purchaseTime;

  private List<UUID> receiptItemsIds;

  @Transient
  private List<ReceiptItem> receiptItems;

  private BigDecimal total;
}
