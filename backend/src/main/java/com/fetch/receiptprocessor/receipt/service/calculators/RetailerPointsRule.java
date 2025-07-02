package com.fetch.receiptprocessor.receipt.service.calculators;

import com.fetch.receiptprocessor.receipt.model.Receipt;
import org.springframework.stereotype.Component;

@Component
public class RetailerPointsRule implements PointsRule {
  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
    if(receipt.getRetailer() != null) {
      return (int) receipt.getRetailer().chars().filter(Character::isLetterOrDigit).count();
    }
    return 0;
  }
}
