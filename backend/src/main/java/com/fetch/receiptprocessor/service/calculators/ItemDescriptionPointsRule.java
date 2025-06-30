package com.fetch.receiptprocessor.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ItemDescriptionPointsRule implements PointsRule {
  @Override
  public int apply(Receipt receipt, boolean llmGenerated) {
    return receipt.getReceiptItems().stream()
            .filter(item -> item.getShortDescription().trim().length() % 3 == 0)
            .map(item -> item.getPrice()
                    .multiply(new BigDecimal("0.2")))
            .map(points -> points.setScale(0, RoundingMode.CEILING))
            .map(BigDecimal::intValueExact)
            .reduce(0, Integer::sum);
  }
}
