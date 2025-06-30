package com.fetch.receiptprocessor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import constants.ApplicationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Receipt data transfer object")
@Data
public class ReceiptDTO {

  @NotNull(message = "retailer is missing.")
  @Pattern(regexp = "^[\\w\\s\\-&]+$", message = "The name of the retailer is invalid.")
  @Schema(description = "Retailer name", example = "Target")
  private String retailer;

  @NotNull(message = "purchase date is missing.")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ApplicationConstants.DATE_FORMAT)
  @Schema(description = "Purchase date in YYYY-MM-DD format", example = "2023-01-15")
  private LocalDate purchaseDate;

  @NotNull(message = "purchase time is missing.")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ApplicationConstants.TIME_FORMAT)
  @Schema(description = "Purchase time in HH:MM format", example = "14:33")
  private LocalTime purchaseTime;

  @NotNull(message = "items is missing.")
  @Size(min = 1, message = "minimum number of items in a receipt should be 1.")
  @Valid
  @Schema(description = "List of items purchased", ref = "ReceiptItemDTO")
  private List<ReceiptItemDTO> items;

  @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "the total of the receipt is invalid.")
  @Schema(description = "Total amount", example = "35.35")
  private String total;

}
