package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.dto.ReceiptItemDTO;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.model.ReceiptItem;
import com.fetch.receiptprocessor.repository.ReceiptItemRepository;
import com.fetch.receiptprocessor.repository.ReceiptRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
    receipt.setReceiptItems(itemIds);
    Receipt savedReceipt = receiptRepository.save(receipt);
    return savedReceipt.getId();
  }

  @Override
  public int getPoints(String id) {
    return 0;
  }
}
