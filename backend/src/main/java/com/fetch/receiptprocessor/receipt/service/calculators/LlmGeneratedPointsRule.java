package com.fetch.receiptprocessor.receipt.service.calculators;

import com.fetch.receiptprocessor.receipt.model.Receipt;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class LlmGeneratedPointsRule implements PointsRule {
  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
    return llmGenerated && receipt.getTotal().compareTo(new BigDecimal("10.00")) > 0 ? 5 : 0;
  }
}
