package com.fetch.receiptprocessor.receipt.service.calculators;

import com.fetch.receiptprocessor.receipt.model.Receipt;

public interface PointsRule {
  int apply(Receipt receipt, boolean llmGenerated);
}
