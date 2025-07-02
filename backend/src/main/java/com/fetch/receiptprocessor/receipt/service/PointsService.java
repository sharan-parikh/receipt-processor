package com.fetch.receiptprocessor.receipt.service;

import com.fetch.receiptprocessor.receipt.model.Receipt;

public interface PointsService {
  int calculatePoints(Receipt receipt, boolean llmGenerated);
}
