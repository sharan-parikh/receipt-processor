package com.fetch.receiptprocessor.unit.service;

import com.fetch.receiptprocessor.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.model.ReceiptItem;
import com.fetch.receiptprocessor.repository.ReceiptItemRepository;
import com.fetch.receiptprocessor.repository.ReceiptRepository;
import com.fetch.receiptprocessor.service.ReceiptProcessorServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptProcessorServiceImplTest {

  @Mock
  private ReceiptRepository receiptRepository;

  @Mock
  private ReceiptItemRepository receiptItemRepository;

  @InjectMocks
  private ReceiptProcessorServiceImpl receiptProcessorService;

  private Receipt testReceipt;
  private ReceiptItem testItem1, testItem2;

  @BeforeEach
  void setUp() {
    // Setup test data
    testItem1 = new ReceiptItem("Item 1", new BigDecimal("10.00"));
    testItem1.setId(UUID.randomUUID());

    testItem2 = new ReceiptItem("Item 2", new BigDecimal("15.00"));
    testItem2.setId(UUID.randomUUID());

    testReceipt = new Receipt();
    testReceipt.setId(UUID.randomUUID());
    testReceipt.setRetailer("Test Retailer");
    testReceipt.setTotal(new BigDecimal("25.00"));
    testReceipt.setReceiptItemsIds(Arrays.asList(testItem1.getId(), testItem2.getId()));
  }

  // getReceiptWithItems tests
  @Test
  void getReceiptWithItems_ValidId_ReturnsReceiptWithItems() throws Exception {
    when(receiptRepository.findById(testReceipt.getId()))
            .thenReturn(Optional.of(testReceipt));
    when(receiptItemRepository.findAllById(testReceipt.getReceiptItemsIds()))
            .thenReturn(Arrays.asList(testItem1, testItem2));

    Receipt result = receiptProcessorService.getReceiptWithItems(testReceipt.getId().toString());

    assertNotNull(result.getReceiptItems());
    assertEquals(2, result.getReceiptItems().size());
    verify(receiptItemRepository, times(1)).findAllById(anyList());
  }

  @Test
  void getReceiptWithItems_NoItems_ReturnsEmptyList() throws Exception {
    testReceipt.setReceiptItemsIds(Collections.emptyList());
    when(receiptRepository.findById(testReceipt.getId()))
            .thenReturn(Optional.of(testReceipt));

    Receipt result = receiptProcessorService.getReceiptWithItems(testReceipt.getId().toString());

    assertTrue(result.getReceiptItems().isEmpty());
  }

  @Test
  void getReceiptWithItems_MissingItems_ThrowsException() {
    // Arrange
    when(receiptRepository.findById(testReceipt.getId()))
            .thenReturn(Optional.of(testReceipt));
    when(receiptItemRepository.findAllById(testReceipt.getReceiptItemsIds()))
            .thenReturn(Collections.singletonList(testItem1)); // Only return 1 of 2 items

    // Act & Assert
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
      receiptProcessorService.getReceiptWithItems(testReceipt.getId().toString());
    });

    assertTrue(exception.getMessage().contains("not found"));
  }

  @Test
  void getReceiptWithItems_InvalidId_ThrowsException() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> {
      receiptProcessorService.getReceiptWithItems("invalid-uuid");
    });
  }

  // Original getReceipt tests remain unchanged
  @Test
  void getReceipt_ExistingId_ReturnsReceipt() throws Exception {
    when(receiptRepository.findById(testReceipt.getId()))
            .thenReturn(Optional.of(testReceipt));

    Receipt result = receiptProcessorService.getReceipt(testReceipt.getId().toString());
    assertEquals(testReceipt.getId(), result.getId());
  }

  @Test
  void getReceiptWithItems_ValidId_MakesSingleQuery() throws Exception {
    when(receiptRepository.findById(testReceipt.getId())).thenReturn(Optional.of(testReceipt));
    when(receiptItemRepository.findAllById(anyList())).thenReturn(List.of(testItem1, testItem2));

    receiptProcessorService.getReceiptWithItems(testReceipt.getId().toString());

    verify(receiptItemRepository, times(1)).findAllById(testReceipt.getReceiptItemsIds());
  }
}
