package com.receiptprocessor.backend.receipt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Document("receipts")
@Data
public class Receipt {

  @Id
  private UUID id;

  private String retailer;

  private LocalDateTime purchaseDateTime;

  private List<UUID> receiptItemsIds;

  @Transient
  private List<ReceiptItem> receiptItems;

  private BigDecimal total;

  public Receipt() {
    this.id = UUID.randomUUID();
  }
}
