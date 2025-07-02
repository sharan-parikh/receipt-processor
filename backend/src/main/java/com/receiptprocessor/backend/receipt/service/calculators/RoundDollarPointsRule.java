package com.receiptprocessor.backend.receipt.service.calculators;

import com.receiptprocessor.backend.receipt.model.Receipt;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class RoundDollarPointsRule implements PointsRule {

  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
   return receipt.getTotal().remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0 ? 50 : 0;
  }
}
