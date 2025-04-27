package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.dto.ReceiptItemDTO;
import com.fetch.receiptprocessor.exception.ReceiptNotFoundException;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.model.ReceiptItem;
import com.fetch.receiptprocessor.repository.ReceiptItemRepository;
import com.fetch.receiptprocessor.repository.ReceiptRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReceiptProcessorServiceImpl implements ReceiptProcessorService {

  private ReceiptRepository receiptRepository;

  private ReceiptItemRepository receiptItemRepository;

  public ReceiptProcessorServiceImpl(
          ReceiptRepository receiptRepository,
          ReceiptItemRepository receiptItemRepository
  ) {
    this.receiptRepository = receiptRepository;
    this.receiptItemRepository = receiptItemRepository;
  }

  @Override
  @Transactional
  public String processReceipt(ReceiptDTO receiptRequest) {
    Receipt receipt = new Receipt();
    receipt.setRetailer(receiptRequest.getRetailer());

    List<ReceiptItem> itemsToAdd = new ArrayList<>();
    for(ReceiptItemDTO item : receiptRequest.getItems()) {
      itemsToAdd.add(new ReceiptItem(item.getShortDescription(), new BigDecimal(item.getPrice())));
    }
    List<ReceiptItem> savedItems = receiptItemRepository.saveAll(itemsToAdd);
    List<String> itemIds = savedItems.stream()
            .map(ReceiptItem::getId)
            .collect(Collectors.toList());
    receipt.setReceiptItemsIds(itemIds);
    Receipt savedReceipt = receiptRepository.save(receipt);
    return savedReceipt.getId();
  }

  @Override
  public Receipt getReceipt(String id) throws ReceiptNotFoundException {
    Optional<Receipt> receipt = receiptRepository.findById(id);
    return receipt.orElseThrow(() -> new ReceiptNotFoundException("A receipt with the given id does not exist"));
  }

  @Override
  public Receipt getReceiptWithItems(String id) throws ReceiptNotFoundException {
    Receipt receipt = getReceipt(id);
    receipt.setReceiptItems(receipt.getReceiptItemsIds().stream().map(itemId -> receiptItemRepository.findById(itemId).orElseThrow()).collect(Collectors.toList()));
    return receipt;
  }
}
