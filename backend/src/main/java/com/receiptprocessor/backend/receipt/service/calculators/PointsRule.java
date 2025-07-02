package com.receiptprocessor.backend.receipt.service.calculators;

import com.receiptprocessor.backend.receipt.model.Receipt;

public interface PointsRule {
  int apply(Receipt receipt, boolean llmGenerated);
}
