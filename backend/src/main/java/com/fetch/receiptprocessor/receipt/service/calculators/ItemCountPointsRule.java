package com.fetch.receiptprocessor.receipt.service.calculators;

import com.fetch.receiptprocessor.receipt.model.Receipt;

import org.springframework.stereotype.Component;

@Component
public class ItemCountPointsRule implements PointsRule {

  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
    int itemCount = receipt.getReceiptItems().size();
    return (itemCount/2) * 5;
  }
}
