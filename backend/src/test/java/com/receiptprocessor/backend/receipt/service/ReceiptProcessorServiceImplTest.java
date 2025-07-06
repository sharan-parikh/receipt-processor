package com.receiptprocessor.backend.receipt.service;

import com.receiptprocessor.backend.common.exception.ResourceAlreadyExistsException;
import com.receiptprocessor.backend.common.exception.ResourceNotFoundException;
import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.model.ReceiptItem;
import com.receiptprocessor.backend.receipt.model.ReceiptPoints;
import com.receiptprocessor.backend.receipt.repository.ReceiptPointsRepository;
import com.receiptprocessor.backend.receipt.repository.ReceiptRepository;

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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptProcessorServiceImplTest {

  @Mock
  private ReceiptRepository receiptRepository;

  @Mock
  private ReceiptPointsRepository receiptPointsRepository;

  @InjectMocks
  private ReceiptProcessorServiceImpl receiptProcessorService;

  private Receipt testReceipt;
  private ReceiptItem testItem1, testItem2;

  @BeforeEach
  void setUp() {
    // Setup test data
    testItem1 = new ReceiptItem("Item 1", new BigDecimal("10.00"));

    testItem2 = new ReceiptItem("Item 2", new BigDecimal("15.00"));

    testReceipt = new Receipt();
    testReceipt.setId(UUID.randomUUID());
    testReceipt.setRetailer("Test Retailer");
    testReceipt.setTotal(new BigDecimal("25.00"));
    testReceipt.setReceiptItems(Arrays.asList(testItem1, testItem2));
  }

  @Test
  void getReceipt_ExistingId_ReturnsReceipt() throws Exception {
    when(receiptRepository.findById(testReceipt.getId()))
            .thenReturn(Optional.of(testReceipt));

    Receipt result = receiptProcessorService.getReceipt(testReceipt.getId().toString());
    assertEquals(testReceipt.getId(), result.getId());
  }

  @Test
  void savePoints_shouldThrowException_WhenPointsExists() {
    when(receiptPointsRepository.findByReceiptId(any(UUID.class))).thenReturn(Optional.of(new ReceiptPoints()));
    assertThrows(ResourceAlreadyExistsException.class, () -> receiptProcessorService.savePoints(UUID.randomUUID(), 10));
  }

  @Test
  void savePoints_shouldNotThrowException_WhenPointsDoesNotExists() throws ResourceAlreadyExistsException {
    when(receiptPointsRepository.findByReceiptId(any(UUID.class))).thenReturn(Optional.empty());
    when(receiptPointsRepository.save(any(ReceiptPoints.class))).thenReturn(new ReceiptPoints());
    receiptProcessorService.savePoints(UUID.randomUUID(), 10);
    verify(receiptPointsRepository, times(1)).save(any(ReceiptPoints.class));
  }
}
