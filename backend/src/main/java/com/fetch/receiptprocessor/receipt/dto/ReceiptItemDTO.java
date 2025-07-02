package com.fetch.receiptprocessor.receipt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Receipt item data transfer object")
@Data
@NoArgsConstructor
public class ReceiptItemDTO {

  @Schema(description = "Short description of an item", example = "Item 1")
  @NotNull(message = "short description of an item is missing")
  @Pattern(regexp = "^[\\w\\s\\-]+$", message = "short description of an item is invalid")
  private String shortDescription;

  @Schema(description = "Price of an item", example = "10.00")
  @NotNull(message = "price of an item is missing")
  @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "price of an item is invalid")
  private String price;

  
  public ReceiptItemDTO(String shortDescription, String price) {
    this.shortDescription = shortDescription;
    this.price = price;
  }
}
