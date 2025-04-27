package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.exception.ReceiptNotFoundException;
import com.fetch.receiptprocessor.model.Receipt;

import java.math.BigDecimal;

public interface ReceiptProcessorService {

  String processReceipt(ReceiptDTO receipt);

  Receipt getReceipt(String id) throws ReceiptNotFoundException;

  Receipt getReceiptWithItems(String id) throws ReceiptNotFoundException;
}
