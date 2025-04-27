package com.fetch.receiptprocessor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("receipts")
@Data
@NoArgsConstructor
public class Receipt {

  @Id
  private String id;

  private String retailer;

  private List<String> receiptItems;

}
