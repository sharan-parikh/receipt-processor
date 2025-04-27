package com.fetch.receiptprocessor.unit.service;

import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.dto.ReceiptItemDTO;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.model.ReceiptItem;
import com.fetch.receiptprocessor.repository.ReceiptItemRepository;
import com.fetch.receiptprocessor.repository.ReceiptRepository;
import com.fetch.receiptprocessor.service.ReceiptProcessorServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReceiptProcessorServiceTest {

  @Mock
  ReceiptRepository receiptRepository;

  @Mock
  ReceiptItemRepository receiptItemRepository;

  @InjectMocks
  ReceiptProcessorServiceImpl service;

  private ReceiptDTO buildDto(String retailer, ReceiptItemDTO... items) {
    ReceiptDTO dto = new ReceiptDTO();
    dto.setRetailer(retailer);
    dto.setPurchaseDate(LocalDate.now());
    dto.setPurchaseTime(LocalTime.now());
    dto.setItems(Arrays.asList(items));
    return dto;
  }

  @Test
  void processReceipt_withItems_convertsAndSavesEverything_andReturnsId() {
    // Arrange
    ReceiptItemDTO dto1 = new ReceiptItemDTO("Apple",  "1.20");
    ReceiptItemDTO dto2 = new ReceiptItemDTO("Banana", "0.80");
    ReceiptDTO input = buildDto("MyShop", dto1, dto2);

    // stub saveAll(...) on receiptItemRepository
    List<ReceiptItem> fakeSavedItems = List.of(
            new ReceiptItem("Apple",  new BigDecimal("1.20")),
            new ReceiptItem("Banana", new BigDecimal("0.80"))
    );
    when(receiptItemRepository.saveAll(anyList()))
            .thenReturn(fakeSavedItems);

    Receipt savedReceipt = new Receipt();
    savedReceipt.setId("ABC-123");
    when(receiptRepository.save(any(Receipt.class)))
            .thenReturn(savedReceipt);

    // Act
    String resultId = service.processReceipt(input);

    // Assert return value
    assertEquals("ABC-123", resultId);

    // Verify items were converted and passed into saveAll(...)
    ArgumentCaptor<List<ReceiptItem>> itemsCaptor =
            ArgumentCaptor.forClass(List.class);
    verify(receiptItemRepository).saveAll(itemsCaptor.capture());
    List<ReceiptItem> toSave = itemsCaptor.getValue();
    assertEquals(2, toSave.size());
    assertEquals("Apple",  toSave.get(0).getShortDescription());
    assertEquals(new BigDecimal("1.20"), toSave.get(0).getPrice());
    assertEquals("Banana", toSave.get(1).getShortDescription());
    assertEquals(new BigDecimal("0.80"), toSave.get(1).getPrice());

    // Verify receipt was constructed correctly
    ArgumentCaptor<Receipt> receiptCaptor =
            ArgumentCaptor.forClass(Receipt.class);
    verify(receiptRepository).save(receiptCaptor.capture());
    Receipt toSaveReceipt = receiptCaptor.getValue();
    assertEquals("MyShop", toSaveReceipt.getRetailer());
    assertTrue(toSaveReceipt.getReceiptItems().isEmpty());

    verifyNoMoreInteractions(receiptItemRepository, receiptRepository);
  }

  @Test
  void processReceipt_withEmptyItems_savesEmptyAndReturnsId() {
    // Arrange
    ReceiptDTO input = buildDto("EmptyShop");  // no items

    when(receiptItemRepository.saveAll(anyList()))
            .thenReturn(Collections.emptyList());

    Receipt saved = new Receipt();
    saved.setId("EMPTY-001");
    when(receiptRepository.save(any(Receipt.class)))
            .thenReturn(saved);

    // Act
    String id = service.processReceipt(input);

    // Assert
    assertEquals("EMPTY-001", id);
    verify(receiptItemRepository).saveAll(Collections.emptyList());
    verify(receiptRepository).save(any(Receipt.class));
    verifyNoMoreInteractions(receiptItemRepository, receiptRepository);
  }

  @Test
  void processReceipt_nullItems_throwsNPE_andNoRepoCalls() {
    // Arrange
    ReceiptDTO input = new ReceiptDTO();
    input.setRetailer("NullShop");
    input.setPurchaseDate(LocalDate.now());
    input.setPurchaseTime(LocalTime.now());
    input.setItems(null);

    // Act & Assert
    assertThrows(NullPointerException.class, () ->
            service.processReceipt(input)
    );

    verifyNoInteractions(receiptItemRepository, receiptRepository);
  }

  @Test
  void processReceipt_itemsThenReceipt_callOrder() {
    // Arrange
    ReceiptItemDTO dtoItem = new ReceiptItemDTO("X", "2.00");
    ReceiptDTO input = buildDto("OrderShop", dtoItem);

    when(receiptItemRepository.saveAll(anyList()))
            .thenReturn(List.of(new ReceiptItem("X", new BigDecimal("2.00"))));

    Receipt saved = new Receipt();
    saved.setId("ORD-1");
    when(receiptRepository.save(any(Receipt.class))).thenReturn(saved);

    // Act
    service.processReceipt(input);

    // Assert call order
    InOrder inOrder = inOrder(receiptItemRepository, receiptRepository);
    inOrder.verify(receiptItemRepository).saveAll(anyList());
    inOrder.verify(receiptRepository).save(any(Receipt.class));
    verifyNoMoreInteractions(receiptItemRepository, receiptRepository);
  }

}
