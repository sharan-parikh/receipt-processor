package com.fetch.receiptprocessor.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetch.receiptprocessor.receipt.dto.ReceiptCreatedResponse;
import com.fetch.receiptprocessor.receipt.dto.ReceiptDTO;
import com.fetch.receiptprocessor.receipt.dto.ReceiptItemDTO;
import com.fetch.receiptprocessor.receipt.model.Receipt;
import com.fetch.receiptprocessor.receipt.model.ReceiptItem;
import com.fetch.receiptprocessor.receipt.repository.ReceiptItemRepository;
import com.fetch.receiptprocessor.receipt.repository.ReceiptRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SimpleIntegrationTests extends AbstractBaseIntegrationTest {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  private ReceiptRepository receiptRepository;

  @Autowired
  private ReceiptItemRepository receiptItemRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private ReceiptDTO mockReceiptDTO;

  @BeforeEach
  public void setUp() {
    mockReceiptDTO = new ReceiptDTO();
    mockReceiptDTO.setRetailer("Target");
    mockReceiptDTO.setPurchaseDate(LocalDate.parse("2022-01-01"));
    mockReceiptDTO.setPurchaseTime(LocalTime.parse("13:01"));
    mockReceiptDTO.setTotal("35.35");

    ReceiptItemDTO item = new ReceiptItemDTO();
    item.setShortDescription("Mountain Dew 12PK");
    item.setPrice("6.49");
    mockReceiptDTO.setItems(List.of(item));
  }

  @AfterEach
  public void tearDown() {
    receiptRepository.deleteAll();
    receiptItemRepository.deleteAll();
  }

  @Test
  public void whenProcessReceiptCalled_withReceipt_ThenReceiptIsSaved() throws Exception {
    MvcResult result = mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mockReceiptDTO)))
                    .andExpect(status().isOk())
                            .andReturn();
    ReceiptCreatedResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), ReceiptCreatedResponse.class);
    Optional<Receipt> savedReceipt = receiptRepository.findById(UUID.fromString(response.getId()));

    assertTrue(!savedReceipt.isEmpty());
    assertEquals(response.getId(), savedReceipt.get().getId().toString());
    assertEquals(new BigDecimal(mockReceiptDTO.getTotal()), savedReceipt.get().getTotal());
    assertEquals(mockReceiptDTO.getPurchaseDate(), mockReceiptDTO.getPurchaseDate());
    assertEquals(mockReceiptDTO.getPurchaseTime(), mockReceiptDTO.getPurchaseTime());
    assertEquals(mockReceiptDTO.getItems().size(), savedReceipt.get().getReceiptItemsIds().size());

    List<ReceiptItem> savedReceiptItems = receiptItemRepository.findAllById(savedReceipt.get().getReceiptItemsIds());

    assertEquals(savedReceiptItems.size(), mockReceiptDTO.getItems().size());
    assertEquals(savedReceiptItems.get(0).getShortDescription(), mockReceiptDTO.getItems().get(0).getShortDescription());
    assertEquals(savedReceiptItems.get(0).getPrice(), new BigDecimal(mockReceiptDTO.getItems().get(0).getPrice()));
  }

  @Test
  public void whenProcessReceiptCalled_withInValidReceipt_ThenReceiptIsNotSaved() throws Exception {
    mockReceiptDTO.setRetailer(null);
    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockReceiptDTO)))
            .andExpect(status().isBadRequest())
            .andReturn();

    List<Receipt> savedReceipts = receiptRepository.findAll();
    List<ReceiptItem> savedReceiptItems = receiptItemRepository.findAll();

    assertTrue(savedReceipts.isEmpty());
    assertTrue(savedReceiptItems.isEmpty());
  }

  @Test
  public void whenProcessReceiptCalled_withOneInvalidItem_receiptShouldNotBeSaved() throws Exception {
    ReceiptItemDTO item = new ReceiptItemDTO();
    item.setShortDescription("Sam Adams 6PK");
    item.setPrice("11.1.0"); // Incorrect price format
    mockReceiptDTO.setItems(List.of(item, mockReceiptDTO.getItems().get(0)));

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockReceiptDTO)))
            .andExpect(status().isBadRequest())
            .andReturn();

    List<Receipt> savedReceipts = receiptRepository.findAll();
    List<ReceiptItem> savedReceiptItems = receiptItemRepository.findAll();

    assertTrue(savedReceipts.isEmpty());
    assertTrue(savedReceiptItems.isEmpty());
  }
}
