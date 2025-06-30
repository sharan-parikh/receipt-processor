package com.fetch.receiptprocessor.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class PurchaseTimePointsRule implements PointsRule {

  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
    return receipt.getPurchaseDateTime().toLocalTime().isAfter(LocalTime.of(14, 0)) &&
            receipt.getPurchaseDateTime().toLocalTime().isBefore(LocalTime.of(16, 0)) ? 10 : 0;
  }
}
