package com.fetch.receiptprocessor.controller;

import com.fetch.receiptprocessor.dto.PointsAwardedResponse;
import com.fetch.receiptprocessor.dto.ReceiptCreatedResponse;
import com.fetch.receiptprocessor.dto.ReceiptDTO;
import com.fetch.receiptprocessor.exception.ResourceNotFoundException;
import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.PointsService;
import com.fetch.receiptprocessor.service.ReceiptProcessorService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/receipts")
@Validated
public class ReceiptProcessorController {
  private final ReceiptProcessorService receiptProcessorService;
  private final PointsService pointsService;

  public ReceiptProcessorController(ReceiptProcessorService receiptProcessorService, PointsService pointsService)  {
    this.receiptProcessorService = receiptProcessorService;
    this.pointsService = pointsService;
  }

  @PostMapping("/process")
  public ResponseEntity<ReceiptCreatedResponse> processReceipt(@RequestBody @Valid ReceiptDTO receiptDTO) {
     Receipt savedReceipt = receiptProcessorService.processReceipt(receiptDTO);
     ReceiptCreatedResponse response = new ReceiptCreatedResponse();
     response.setId(savedReceipt.getId().toString());
     return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{id}/points")
  public ResponseEntity<PointsAwardedResponse> getPoints(
          @PathVariable(name = "id") @NotBlank(message = "receipt id is invalid or missing") String receiptId) throws ResourceNotFoundException {
    Receipt receipt = receiptProcessorService.getReceiptWithItems(receiptId);
    PointsAwardedResponse response = new PointsAwardedResponse();
    response.setPoints(pointsService.calculatePoints(receipt, false));
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
