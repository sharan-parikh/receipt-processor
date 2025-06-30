package com.fetch.receiptprocessor.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;

public interface PointsRule {
  int apply(Receipt receipt, boolean llmGenerated);
}
