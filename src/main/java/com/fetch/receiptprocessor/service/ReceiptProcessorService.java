package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dto.ReceiptDTO;

public interface ReceiptProcessorService {

  String processReceipt(ReceiptDTO receipt);

  int getPoints(String id);
}
