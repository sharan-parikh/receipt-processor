package com.receiptprocessor.backend.receipt.service;

import com.receiptprocessor.backend.receipt.dto.ReceiptDTO;
import com.receiptprocessor.backend.common.exception.ResourceAlreadyExistsException;
import com.receiptprocessor.backend.common.exception.ResourceNotFoundException;
import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.model.ReceiptPoints;

import java.util.UUID;

public interface ReceiptProcessorService {

  Receipt saveReceipt(ReceiptDTO receipt);

  Receipt getReceipt(String id) throws ResourceNotFoundException;

  ReceiptPoints savePoints(UUID receiptId, int points) throws ResourceAlreadyExistsException;

  ReceiptPoints getPoints(String receiptId) throws ResourceNotFoundException;
}
