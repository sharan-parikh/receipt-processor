package com.fetch.receiptprocessor;

import com.fetch.receiptprocessor.controller.ReceiptProcessorController;
import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.dto.ReceiptItemDTO;
import com.fetch.receiptprocessor.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.repository.ReceiptItemRepository;
import com.fetch.receiptprocessor.repository.ReceiptRepository;
import com.fetch.receiptprocessor.service.PointsService;
import com.fetch.receiptprocessor.service.ReceiptProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReceiptProcessorController.class)
public class ReceiptProcessorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ReceiptProcessorService receiptProcessorService;


  @MockBean
  private PointsService pointsService;

  @MockBean
  private ReceiptRepository receiptRepository;

  @MockBean
  private ReceiptItemRepository receiptItemRepository;

  private ReceiptDTO createValidReceiptDTO() {
    ReceiptDTO receiptDTO = new ReceiptDTO();
    receiptDTO.setRetailer("Test Retailer");
    receiptDTO.setPurchaseDate(LocalDate.parse("2023-06-15"));
    receiptDTO.setPurchaseTime(LocalTime.parse("15:30"));
    receiptDTO.setTotal("35.75");

    List<ReceiptItemDTO> items = new ArrayList<>();
    ReceiptItemDTO item1 = new ReceiptItemDTO();
    item1.setShortDescription("Test Item 1");
    item1.setPrice("10.00");
    items.add(item1);

    receiptDTO.setItems(items);
    return receiptDTO;
  }

  @Test
  void processReceipt_ValidInput_Returns201() throws Exception {
    ReceiptDTO receiptDTO = createValidReceiptDTO();
    Receipt mockReceipt = new Receipt();
    mockReceipt.setId(UUID.randomUUID());

    when(receiptProcessorService.processReceipt(any(ReceiptDTO.class))).thenReturn(mockReceipt);

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receiptDTO)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
  }

  @Test
  void processReceipt_InvalidInput_Returns400() throws Exception {
    ReceiptDTO invalidReceipt = new ReceiptDTO(); // Empty/invalid object

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidReceipt)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.retailer").exists()); // Assuming retailer is a required field
  }

  @Test
  void getPoints_ValidId_Returns200WithPoints() throws Exception {
    String validId = UUID.randomUUID().toString();
    Receipt mockReceipt = new Receipt();

    when(receiptProcessorService.getReceiptWithItems(validId)).thenReturn(mockReceipt);
    when(pointsService.calculatePoints(mockReceipt, false)).thenReturn(100);

    mockMvc.perform(get("/receipts/{id}/points", validId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.points").value(100));
  }

  @Test
  void getPoints_BlankId_Returns400WithErrorMessage() throws Exception {
    mockMvc.perform(get("/receipts/{id}/points", " "))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors[0]").value("getPoints.receiptId: receipt id is invalid or missing"));
  }

  @Test
  void getPoints_NonExistentId_Returns404() throws Exception {
    String nonExistentId = UUID.randomUUID().toString();

    when(receiptProcessorService.getReceiptWithItems(nonExistentId))
            .thenThrow(new ResourceNotFoundException("Receipt not found"));

    mockMvc.perform(get("/receipts/{id}/points", nonExistentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Receipt not found"));
  }

  @Test
  void processReceipt_ServiceThrowsException_Returns500() throws Exception {
    when(receiptProcessorService.processReceipt(any()))
            .thenThrow(new RuntimeException("Unexpected error"));

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createValidReceiptDTO())))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("An error occurred"));
  }

}
