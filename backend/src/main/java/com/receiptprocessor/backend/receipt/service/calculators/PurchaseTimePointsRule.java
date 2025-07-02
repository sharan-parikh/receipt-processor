package com.receiptprocessor.backend.receipt.service.calculators;

import com.receiptprocessor.backend.receipt.model.Receipt;

import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class PurchaseTimePointsRule implements PointsRule {

  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
    return receipt.getPurchaseDateTime().toLocalTime().isAfter(LocalTime.of(14, 0)) &&
            receipt.getPurchaseDateTime().toLocalTime().isBefore(LocalTime.of(16, 0)) ? 10 : 0;
  }
}
