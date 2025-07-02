package com.receiptprocessor.backend.receipt.service;

import com.receiptprocessor.backend.receipt.model.Receipt;

public interface PointsService {
  int calculatePoints(Receipt receipt, boolean llmGenerated);
}
