package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.exception.ResourceAlreadyExistsException;
import com.fetch.receiptprocessor.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.model.ReceiptPoints;

import java.util.Optional;
import java.util.UUID;

public interface ReceiptProcessorService {

  Receipt processReceipt(ReceiptDTO receipt);

  Receipt getReceipt(String id) throws ResourceNotFoundException;

  Receipt getReceiptWithItems(String id) throws ResourceNotFoundException;

  ReceiptPoints savePoints(UUID receiptId, int points) throws ResourceAlreadyExistsException;

  Optional<ReceiptPoints> getPoints(UUID receiptId);
}
