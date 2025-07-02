package com.fetch.receiptprocessor.receipt.service;

import com.fetch.receiptprocessor.receipt.dto.ReceiptDTO;
import com.fetch.receiptprocessor.receipt.dto.ReceiptItemDTO;
import com.fetch.receiptprocessor.common.exception.ResourceAlreadyExistsException;
import com.fetch.receiptprocessor.common.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.receipt.model.Receipt;
import com.fetch.receiptprocessor.receipt.model.ReceiptItem;
import com.fetch.receiptprocessor.receipt.model.ReceiptPoints;
import com.fetch.receiptprocessor.receipt.repository.ReceiptItemRepository;
import com.fetch.receiptprocessor.receipt.repository.ReceiptPointsRepository;
import com.fetch.receiptprocessor.receipt.repository.ReceiptRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.fetch.receiptprocessor.common.utils.ExceptionMessages;

@Service
public class ReceiptProcessorServiceImpl implements ReceiptProcessorService {

  private final ReceiptRepository receiptRepository;

  private final ReceiptItemRepository receiptItemRepository;

  private final ReceiptPointsRepository receiptPointsRepository;

  private final PointsService pointsService;

  public ReceiptProcessorServiceImpl(
          ReceiptRepository receiptRepository,
          ReceiptItemRepository receiptItemRepository,
          ReceiptPointsRepository receiptPointsRepository,
          PointsService pointsService
  ) {
    this.receiptRepository = receiptRepository;
    this.receiptItemRepository = receiptItemRepository;
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
    List<ReceiptItem> savedItems = receiptItemRepository.saveAll(itemsToAdd);
    List<UUID> itemIds = savedItems.stream()
            .map(ReceiptItem::getId)
            .collect(Collectors.toList());
    receipt.setReceiptItemsIds(itemIds);
    Receipt savedReceipt = receiptRepository.save(receipt);
    return savedReceipt;
  }

  @Override
  public Receipt getReceipt(String id) throws ResourceNotFoundException {
    Optional<Receipt> receipt = receiptRepository.findById(UUID.fromString(id));
    return receipt.orElseThrow(() -> new ResourceNotFoundException(ExceptionMessages.RECEIPT_NOT_FOUND));
  }

  @Override
  public Receipt getReceiptWithItems(String id) throws ResourceNotFoundException {
    Receipt receipt = getReceipt(id);
    List<ReceiptItem> items = receiptItemRepository.findAllById(receipt.getReceiptItemsIds());

    if (items.size() != receipt.getReceiptItemsIds().size()) {
      throw new ResourceNotFoundException(
              items.isEmpty() ? "No items found" : "Some items not found"
      );
    }
    receipt.setReceiptItems(items);
    return receipt;
  }

  @Override
  public void populateReceiptItems(Receipt receipt) throws ResourceNotFoundException {
    receipt.setReceiptItems(new ArrayList<>());
    for(UUID itemId : receipt.getReceiptItemsIds()) {
      Optional<ReceiptItem> item = receiptItemRepository.findById(itemId);
      if(item.isEmpty()) {
        throw new ResourceNotFoundException("One or more items does not exist");
      }
      receipt.getReceiptItems().add(item.get());
    }
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
  public ReceiptPoints getPoints(String receiptId) throws ResourceNotFoundException {
    UUID receiptUuId = UUID.fromString(receiptId);
    Optional<ReceiptPoints> receiptPoints = receiptPointsRepository.findByReceiptId(receiptUuId);

    if(receiptPoints.isPresent()) {
      return receiptPoints.get();
    }

    Receipt receipt = getReceiptWithItems(receiptId);
    int points = pointsService.calculatePoints(receipt, false);
    ReceiptPoints receiptPointsToSave = new ReceiptPoints();
    receiptPointsToSave.setReceiptId(receiptUuId);
    receiptPointsToSave.setPoints(points);
    return receiptPointsRepository.save(receiptPointsToSave);
  }
}
