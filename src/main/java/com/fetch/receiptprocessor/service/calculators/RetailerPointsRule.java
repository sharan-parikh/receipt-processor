package com.fetch.receiptprocessor.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Component;

@Component
public class RetailerPointsRule implements PointsRule {
  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
    return receipt.getRetailer() != null ? receipt.getRetailer().length() : 0;
  }
}
