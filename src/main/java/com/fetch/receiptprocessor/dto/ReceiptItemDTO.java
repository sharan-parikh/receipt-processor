package com.fetch.receiptprocessor.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReceiptItemDTO {

  @NotNull(message = "short description of an item is missing")
  @Pattern(regexp = "^[\\w\\s\\-]+$", message = "short description of an item is invalid")
  private String shortDescription;

  @NotNull(message = "price of an item is missing")
  @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "price of an item is invalid")
  private String price;

  public ReceiptItemDTO(String shortDescription, String price) {
    this.shortDescription = shortDescription;
    this.price = price;
  }
}
