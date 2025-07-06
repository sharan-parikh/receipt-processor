package com.receiptprocessor.backend.receipt.service;

import com.receiptprocessor.backend.receipt.dto.ReceiptDTO;
import com.receiptprocessor.backend.receipt.dto.ReceiptItemDTO;
import com.receiptprocessor.backend.common.exception.ResourceAlreadyExistsException;
import com.receiptprocessor.backend.common.exception.ResourceNotFoundException;
import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.model.ReceiptItem;
import com.receiptprocessor.backend.receipt.model.ReceiptPoints;
import com.receiptprocessor.backend.receipt.repository.ReceiptPointsRepository;
import com.receiptprocessor.backend.receipt.repository.ReceiptRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.receiptprocessor.backend.common.utils.ExceptionMessages;

@Service
public class ReceiptProcessorServiceImpl implements ReceiptProcessorService {

  private final ReceiptRepository receiptRepository;

  private final ReceiptPointsRepository receiptPointsRepository;

  private final PointsService pointsService;

  public ReceiptProcessorServiceImpl(
          ReceiptRepository receiptRepository,
          ReceiptPointsRepository receiptPointsRepository,
          PointsService pointsService
  ) {
    this.receiptRepository = receiptRepository;
    this.receiptPointsRepository = receiptPointsRepository;
    this.pointsService = pointsService;
  }

  @Override
  public Receipt saveReceipt(ReceiptDTO receiptRequest) {
    Receipt receipt = new Receipt();
    receipt.setRetailer(receiptRequest.getRetailer());
    receipt.setPurchaseDateTime(LocalDateTime.of(receiptRequest.getPurchaseDate(), receiptRequest.getPurchaseTime()));
    receipt.setTotal(new BigDecimal(receiptRequest.getTotal()));

    List<ReceiptItem> itemsToAdd = new ArrayList<>();
    for(ReceiptItemDTO item : receiptRequest.getItems()) {
      itemsToAdd.add(new ReceiptItem(item.getShortDescription(), new BigDecimal(item.getPrice())));
    }

    receipt.setReceiptItems(itemsToAdd);
    Receipt savedReceipt = receiptRepository.save(receipt);
    return savedReceipt;
  }

  @Override
  public Receipt getReceipt(String id) throws ResourceNotFoundException {
    Optional<Receipt> receipt = receiptRepository.findById(UUID.fromString(id));
    return receipt.orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RECEIPT_NOT_FOUND));
  }

  @Override
  public ReceiptPoints savePoints(UUID receiptId, int points) throws ResourceAlreadyExistsException {
    Optional<ReceiptPoints> receiptPoints = receiptPointsRepository.findByReceiptId(receiptId);

    if(receiptPoints.isPresent()) {
      throw new ResourceAlreadyExistsException("Points already calculated for this receipt.");
    }

    ReceiptPoints pointsToSave = new ReceiptPoints();
    pointsToSave.setPoints(points);
    return receiptPointsRepository.save(pointsToSave);
  }

  @Override
  public ReceiptPoints getPoints(UUID receiptId) throws ResourceNotFoundException {
    Optional<ReceiptPoints> receiptPoints = receiptPointsRepository.findByReceiptId(receiptId);

    if(receiptPoints.isPresent()) {
      return receiptPoints.get();
    }

    Receipt receipt = receiptRepository.findById(receiptId).orElseThrow(
            () -> new ResourceNotFoundException(ExceptionMessages.RECEIPT_NOT_FOUND)
    );
    int points = pointsService.calculatePoints(receipt, false);
    ReceiptPoints receiptPointsToSave = new ReceiptPoints();
    receiptPointsToSave.setReceiptId(receiptId);
    receiptPointsToSave.setPoints(points);
    return receiptPointsRepository.save(receiptPointsToSave);
  }
}
