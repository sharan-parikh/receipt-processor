package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.model.Receipt;

public interface ReceiptProcessorService {

  Receipt processReceipt(ReceiptDTO receipt);

  Receipt getReceipt(String id) throws ResourceNotFoundException;

  Receipt getReceiptWithItems(String id) throws ResourceNotFoundException;
}
