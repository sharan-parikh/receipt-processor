package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.dto.ReceiptItemDTO;
import com.fetch.receiptprocessor.exception.ResourceAlreadyExistsException;
import com.fetch.receiptprocessor.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.model.ReceiptItem;
import com.fetch.receiptprocessor.model.ReceiptPoints;
import com.fetch.receiptprocessor.repository.ReceiptItemRepository;
import com.fetch.receiptprocessor.repository.ReceiptPointsRepository;
import com.fetch.receiptprocessor.repository.ReceiptRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.swing.text.html.Option;

import constants.ExceptionMessages;

@Service
public class ReceiptProcessorServiceImpl implements ReceiptProcessorService {

  private ReceiptRepository receiptRepository;

  private ReceiptItemRepository receiptItemRepository;

  private ReceiptPointsRepository receiptPointsRepository;

  public ReceiptProcessorServiceImpl(
          ReceiptRepository receiptRepository,
          ReceiptItemRepository receiptItemRepository,
          ReceiptPointsRepository receiptPointsRepository
  ) {
    this.receiptRepository = receiptRepository;
    this.receiptItemRepository = receiptItemRepository;
    this.receiptPointsRepository = receiptPointsRepository;
  }

  @Override
  public Receipt processReceipt(ReceiptDTO receiptRequest) {
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
  public Optional<ReceiptPoints> getPoints(UUID receiptId) {
    return receiptPointsRepository.findByReceiptId(receiptId);
  }
}
