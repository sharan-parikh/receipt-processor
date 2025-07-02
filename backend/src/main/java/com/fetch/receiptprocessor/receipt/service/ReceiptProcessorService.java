package com.fetch.receiptprocessor.receipt.service;

import com.fetch.receiptprocessor.receipt.dto.ReceiptDTO;
import com.fetch.receiptprocessor.common.exception.ResourceAlreadyExistsException;
import com.fetch.receiptprocessor.common.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.receipt.model.Receipt;
import com.fetch.receiptprocessor.receipt.model.ReceiptItem;
import com.fetch.receiptprocessor.receipt.model.ReceiptPoints;

import java.util.UUID;

public interface ReceiptProcessorService {

  Receipt saveReceipt(ReceiptDTO receipt);

  Receipt getReceipt(String id) throws ResourceNotFoundException;

  Receipt getReceiptWithItems(String id) throws ResourceNotFoundException;

  void populateReceiptItems(Receipt receipt) throws ResourceNotFoundException;

  ReceiptPoints savePoints(UUID receiptId, int points) throws ResourceAlreadyExistsException;

  ReceiptPoints getPoints(String receiptId) throws ResourceNotFoundException;
}
