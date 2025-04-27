package com.fetch.receiptprocessor.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class QuarterMultipleRule implements PointsRule {
  private static final BigDecimal QUARTER = new BigDecimal("0.25");
  @Override
  public int apply(Receipt r, boolean llmGenerated) {
    return r.getTotal().remainder(QUARTER).compareTo(BigDecimal.ZERO) == 0
            ? 25 : 0;
  }
}
