package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.model.Receipt;

public interface PointsService {
  int calculatePoints(Receipt receipt, boolean llmGenerated);
}
