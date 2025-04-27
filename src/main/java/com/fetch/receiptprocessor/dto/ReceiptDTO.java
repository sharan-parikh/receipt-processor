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

@Data
public class ReceiptDTO {

  @Pattern(regexp = "^[\\w\\s\\-&]+$", message = "The name of the retailer is invalid.")
  private String retailer;

  @NotNull(message = "purchase date is missing or is invalid.")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ApplicationConstants.DATE_FORMAT)
  private LocalDate purchaseDate;

  @NotNull(message = "purchase time is missing or is invalid.")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ApplicationConstants.TIME_FORMAT)
  private LocalTime purchaseTime;

  @Size(min = 1, message = "minimum number of items in a receipt should be 1.")
  @Valid
  private List<ReceiptItemDTO> items;

  @Pattern(regexp = "^\\d+\\.\\d{2}$", message = "the total of the receipt is invalid.")
  private String total;

}
