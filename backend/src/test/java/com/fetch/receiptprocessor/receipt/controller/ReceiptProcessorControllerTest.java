package com.fetch.receiptprocessor.receipt.controller;

import com.fetch.receiptprocessor.receipt.dto.ReceiptDTO;
import com.fetch.receiptprocessor.receipt.dto.ReceiptItemDTO;
import com.fetch.receiptprocessor.common.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.receipt.model.Receipt;
import com.fetch.receiptprocessor.receipt.model.ReceiptPoints;
import com.fetch.receiptprocessor.receipt.service.PointsService;
import com.fetch.receiptprocessor.receipt.service.ReceiptProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = ReceiptProcessorController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class ReceiptProcessorControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ReceiptProcessorService receiptProcessorService;

  @MockitoBean
  private PointsService pointsService;

  @BeforeEach
  void setup() {
    when(receiptProcessorService.saveReceipt(any()))
            .thenAnswer(invocation -> {
              Receipt receipt = new Receipt();
              receipt.setId(UUID.randomUUID());
              return receipt;
            });
  }

  @Test
  public void processReceipt_withValidSingleItem_shouldReturnId() throws Exception {
    ReceiptDTO receipt = new ReceiptDTO();
    receipt.setRetailer("Target");
    receipt.setPurchaseDate(LocalDate.parse("2022-01-01"));
    receipt.setPurchaseTime(LocalTime.parse("13:01"));
    receipt.setTotal("35.35");

    ReceiptItemDTO item = new ReceiptItemDTO();
    item.setShortDescription("Mountain Dew 12PK");
    item.setPrice("6.49");
    receipt.setItems(List.of(item));

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receipt)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  public void processReceipt_withSpecialCharsInRetailer_shouldSucceed() throws Exception {
    ReceiptDTO receipt = new ReceiptDTO();
    receipt.setRetailer("M&M Market-Store & More"); // Valid special chars
    receipt.setPurchaseDate(LocalDate.parse("2022-01-01"));
    receipt.setPurchaseTime(LocalTime.parse("13:01"));
    receipt.setTotal("35.35");
    receipt.setItems(List.of(new ReceiptItemDTO("Mountain Dew 12PK", "6.49")));

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receipt)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
  }

  @Test
  public void processReceipt_withValidMultipleItems_shouldReturnId() throws Exception {
    ReceiptDTO receipt = new ReceiptDTO();
    receipt.setRetailer("M&M Corner Market");
    receipt.setPurchaseDate(LocalDate.parse("2022-03-20"));
    receipt.setPurchaseTime(LocalTime.parse("14:33"));
    receipt.setTotal("9.00");

    ReceiptItemDTO item1 = new ReceiptItemDTO();
    item1.setShortDescription("Gatorade");
    item1.setPrice("2.25");

    ReceiptItemDTO item2 = new ReceiptItemDTO();
    item2.setShortDescription("Gatorade");
    item2.setPrice("2.25");

    ReceiptItemDTO item3 = new ReceiptItemDTO();
    item3.setShortDescription("Gatorade");
    item3.setPrice("2.25");

    ReceiptItemDTO item4 = new ReceiptItemDTO();
    item4.setShortDescription("Gatorade");
    item4.setPrice("2.25");

    receipt.setItems(List.of(item1, item2, item3, item4));

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receipt)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists());
  }

  @Test
  public void processReceipt_withMissingRequiredFields_shouldReturnBadRequest() throws Exception {
    ReceiptDTO receipt = new ReceiptDTO(); // Empty receipt

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receipt)))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void processReceipt_withInvalidDateFormat_shouldReturnBadRequest() throws Exception {
    String jsonWithInvalidDateFormat = """
            {
              "retailer": "Target",
              "purchaseDate": "2022/01/01",
              "purchaseTime": "13:01",
              "items": [
                {
                  "shortDescription": "Mountain Dew 12PK",
                  "price": "6.49"
                },{
                  "shortDescription": "Emils Cheese Pizza",
                  "price": "12.25"
                },{
                  "shortDescription": "Knorr Creamy Chicken",
                  "price": "1.26"
                },{
                  "shortDescription": "Doritos Nacho Cheese",
                  "price": "3.35"
                },{
                  "shortDescription": "   Klarbrunn 12-PK 12 FL OZ  ",
                  "price": "12.00"
                }
              ],
              "total": "35.35"
            }
            """;

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonWithInvalidDateFormat))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void processReceipt_withInvalidTimeFormat_shouldReturnBadRequest() throws Exception {
    String jsonWithInvalidTimeFormat = """
            {
              "retailer": "Target",
              "purchaseDate": "2022-01-01",
              "purchaseTime": "1:01 PM",
              "items": [
                {
                  "shortDescription": "Mountain Dew 12PK",
                  "price": "6.49"
                },{
                  "shortDescription": "Emils Cheese Pizza",
                  "price": "12.25"
                },{
                  "shortDescription": "Knorr Creamy Chicken",
                  "price": "1.26"
                },{
                  "shortDescription": "Doritos Nacho Cheese",
                  "price": "3.35"
                },{
                  "shortDescription": "   Klarbrunn 12-PK 12 FL OZ  ",
                  "price": "12.00"
                }
              ],
              "total": "35.35"
            }
            """;

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonWithInvalidTimeFormat))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void processReceipt_withInvalidTotalFormat_shouldReturnBadRequest() throws Exception {
    ReceiptDTO receipt = new ReceiptDTO();
    receipt.setRetailer("Target");
    receipt.setPurchaseDate(LocalDate.parse("2022-01-01"));
    receipt.setPurchaseTime(LocalTime.parse("13:01"));
    receipt.setTotal("35.355"); // Invalid format
    receipt.setItems(List.of(new ReceiptItemDTO("Mountain Dew 12PK", "6.49")));

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receipt)))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void processReceipt_withEmptyItems_shouldReturnBadRequest() throws Exception {
    ReceiptDTO receipt = new ReceiptDTO();
    receipt.setRetailer("Target");
    receipt.setPurchaseDate(LocalDate.parse("2022-01-01"));
    receipt.setPurchaseTime(LocalTime.parse("13:01"));
    receipt.setTotal("35.35");
    receipt.setItems(List.of());

    mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receipt)))
            .andExpect(status().isBadRequest());
  }

  @Test
  public void getPoints_forExistingReceipt_shouldReturnPoints() throws Exception {
    ReceiptDTO receipt = new ReceiptDTO();
    receipt.setRetailer("Target");
    receipt.setPurchaseDate(LocalDate.parse("2022-01-01"));
    receipt.setPurchaseTime(LocalTime.parse("13:01"));
    receipt.setTotal("35.35");
    receipt.setItems(List.of(new ReceiptItemDTO("Mountain Dew 12PK", "6.49")));

    String response = mockMvc.perform(post("/receipts/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(receipt)))
            .andReturn().getResponse().getContentAsString();

    String receiptId = JsonPath.read(response, "$.id");

    ReceiptPoints mockPoints = new ReceiptPoints();
    mockPoints.setPoints(10);
    when(receiptProcessorService.getPoints(any(String.class))).thenReturn(mockPoints);
    // Then get points
    mockMvc.perform(get("/receipts/{id}/points", receiptId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.points").exists())
            .andExpect(jsonPath("$.points").value(10));
  }

  @Test
  public void getPoints_forNonExistentReceipt_shouldReturnNotFound() throws Exception {
    String nonExistentId = UUID.randomUUID().toString();
    when(receiptProcessorService.getPoints(nonExistentId)).thenThrow(new ResourceNotFoundException("Receipt not found"));

    mockMvc.perform(get("/receipts/{id}/points", nonExistentId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists());
  }

  @Test
  public void getPoints_withBlankId_shouldReturnBadRequest() throws Exception {
    mockMvc.perform(get("/receipts/{id}/points", " "))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").exists());
  }
}