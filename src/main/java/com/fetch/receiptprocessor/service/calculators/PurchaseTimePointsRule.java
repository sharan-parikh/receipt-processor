package com.fetch.receiptprocessor.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Component;
import java.time.LocalTime;

@Component
public class PurchaseTimePointsRule implements PointsRule {

  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
    return receipt.getPurchaseTime().isAfter(LocalTime.of(14, 0)) &&
            receipt.getPurchaseTime().isBefore(LocalTime.of(16, 0)) ? 10 : 0;
  }
}
