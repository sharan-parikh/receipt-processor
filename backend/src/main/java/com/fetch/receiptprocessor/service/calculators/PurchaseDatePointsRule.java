package com.fetch.receiptprocessor.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Component;

@Component
public class PurchaseDatePointsRule implements PointsRule {
  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
    return (receipt.getPurchaseDateTime().getDayOfMonth() % 2 != 0) ? 6 : 0;
  }
}
