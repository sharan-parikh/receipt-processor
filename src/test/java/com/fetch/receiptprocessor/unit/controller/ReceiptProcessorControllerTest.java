package com.fetch.receiptprocessor.unit.controller;

import com.fetch.receiptprocessor.configuration.ValidationConfig;
import com.fetch.receiptprocessor.controller.ReceiptProcessorController;
import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.dto.ReceiptItemDTO;
import com.fetch.receiptprocessor.exception.GlobalExceptionHandler;
import com.fetch.receiptprocessor.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.PointsService;
import com.fetch.receiptprocessor.service.ReceiptProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReceiptProcessorControllerTest {

  private MockMvc mockMvc;

  @Mock
  private ReceiptProcessorService receiptProcessorService;

  @Mock
  private PointsService pointsService;

  @InjectMocks
  private ReceiptProcessorController controller;

  private ObjectMapper objectMapper;
  private ReceiptDTO validReceiptDTO;
  private Receipt mockReceipt;

  @BeforeEach
  void setUp() {
    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new GlobalExceptionHandler()) // Add your global exception handler
            .setValidator(validator)
            .build();

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();

    validReceiptDTO = new ReceiptDTO();
    validReceiptDTO.setRetailer("Target");
    validReceiptDTO.setPurchaseDate(LocalDate.of(2023, 1, 1));
    validReceiptDTO.setPurchaseTime(LocalTime.of(13, 1));
    validReceiptDTO.setItems(Arrays.asList(
            new ReceiptItemDTO("Mountain Dew", "3.99"),
            new ReceiptItemDTO("Pepsi", "2.99")
    ));
    validReceiptDTO.setTotal("6.98");

    mockReceipt = new Receipt();
    mockReceipt.setId("abc123");
    mockReceipt.setRetailer("Target");
    mockReceipt.setPurchaseDate(LocalDate.of(2023, 1, 1));
    mockReceipt.setPurchaseTime(LocalTime.of(13, 1));
    mockReceipt.setTotal(new BigDecimal("6.98"));
  }

  @Test
  void processReceipt_withValidData_returnsSuccessWithId() throws Exception {
    when(receiptProcessorService.processReceipt(any(ReceiptDTO.class))).thenReturn(mockReceipt);

    ResultActions response = mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validReceiptDTO)));

    response.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("abc123")))
            .andDo(print());
  }

  @Test
  void processReceipt_withInvalidRetailer_returnsBadRequest() throws Exception {
    validReceiptDTO.setRetailer("Target@#$");

    ResultActions response = mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validReceiptDTO)));

    response.andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  void processReceipt_withMissingPurchaseDate_returnsBadRequest() throws Exception {
    validReceiptDTO.setPurchaseDate(null);

    ResultActions response = mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validReceiptDTO)));

    response.andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  void processReceipt_withInvalidTotal_returnsBadRequest() throws Exception {
    validReceiptDTO.setTotal("6.987");

    ResultActions response = mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validReceiptDTO)));

    response.andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  void processReceipt_withNoItems_returnsBadRequest() throws Exception {
    validReceiptDTO.setItems(Arrays.asList());

    ResultActions response = mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validReceiptDTO)));

    response.andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  void processReceipt_withInvalidItemDescription_returnsBadRequest() throws Exception {
    validReceiptDTO.setItems(Arrays.asList(
            new ReceiptItemDTO("Mountain Dew", "3.99"),
            new ReceiptItemDTO("Pepsi@#$", "2.99")
    ));

    ResultActions response = mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validReceiptDTO)));

    response.andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  void processReceipt_withInvalidItemPrice_returnsBadRequest() throws Exception {
    validReceiptDTO.setItems(Arrays.asList(
            new ReceiptItemDTO("Mountain Dew", "3.99"),
            new ReceiptItemDTO("Pepsi", "2.99999")
    ));

    ResultActions response = mockMvc.perform(post("/receipts/process")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validReceiptDTO)));

    response.andExpect(status().isBadRequest())
            .andDo(print());
  }

  @Test
  void getPoints_withValidId_returnsPoints() throws Exception {
    when(receiptProcessorService.getReceipt("abc123")).thenReturn(mockReceipt);
    when(pointsService.calculatePoints(mockReceipt, false)).thenReturn(15);

    ResultActions response = mockMvc.perform(get("/receipts/abc123/points")
            .contentType(MediaType.APPLICATION_JSON));

    response.andExpect(status().isOk())
            .andExpect(jsonPath("$.points", is(15)))
            .andDo(print());
  }

  @Test
  void getPoints_withNonExistentId_returnsNotFound() throws Exception {
    when(receiptProcessorService.getReceipt("nonexistent")).thenThrow(new ResourceNotFoundException("Receipt not found"));

    ResultActions response = mockMvc.perform(get("/receipts/nonexistent/points")
            .contentType(MediaType.APPLICATION_JSON));

    response.andExpect(status().isNotFound())
            .andDo(print());
  }

//  @Test
//  void getPoints_withBlankId_returnsBadRequest() throws Exception {
//    ResultActions response = mockMvc.perform(get("/receipts/{id}/points", " ")
//            .contentType(MediaType.APPLICATION_JSON));
//
//    response.andExpect(status().isBadRequest())
//            .andDo(print());
//  }
}