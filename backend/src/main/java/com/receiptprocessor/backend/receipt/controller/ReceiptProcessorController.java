package com.receiptprocessor.backend.receipt.controller;

import com.receiptprocessor.backend.receipt.dto.PointsAwardedResponse;
import com.receiptprocessor.backend.receipt.dto.ReceiptCreatedResponse;
import com.receiptprocessor.backend.receipt.dto.ReceiptDTO;
import com.receiptprocessor.backend.common.exception.ResourceAlreadyExistsException;
import com.receiptprocessor.backend.common.exception.ResourceNotFoundException;
import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.model.ReceiptPoints;
import com.receiptprocessor.backend.receipt.service.PointsService;
import com.receiptprocessor.backend.receipt.service.ReceiptProcessorService;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/receipts")
@Validated
@Tag(name = "Receipt Processing", description = "APIs for processing receipts and calculating points")
public class ReceiptProcessorController {
  private final ReceiptProcessorService receiptProcessorService;

  private final PointsService pointsService;

  public ReceiptProcessorController(ReceiptProcessorService receiptProcessorService, PointsService pointsService)  {
    this.receiptProcessorService = receiptProcessorService;
    this.pointsService = pointsService;
  }

  @Operation(
      summary = "Process a receipt",
      description = "Processes a receipt and calculates points based on various rules"
  )
  @ApiResponse(
      responseCode = "200", 
      description = "Receipt processed successfully",
      content = @Content(schema = @Schema(implementation = ReceiptCreatedResponse.class))
  )
  @ApiResponse(responseCode = "400", description = "Invalid receipt data")
  @PostMapping("/process")
  public ResponseEntity<ReceiptCreatedResponse> processReceipt(
          @Parameter(description = "Receipt data to process") 
          @RequestBody @Valid ReceiptDTO receiptDTO
  ) throws ResourceAlreadyExistsException, ResourceNotFoundException {
    Receipt savedReceipt = receiptProcessorService.saveReceipt(receiptDTO);
    receiptProcessorService.populateReceiptItems(savedReceipt);
    receiptProcessorService.savePoints(savedReceipt.getId(), pointsService.calculatePoints(savedReceipt, false));

    ReceiptCreatedResponse response = new ReceiptCreatedResponse();
    response.setId(savedReceipt.getId().toString());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @Operation(
      summary = "Get points for a receipt",
      description = "Retrieves the points awarded for a specific receipt"
  )
  @ApiResponse(
      responseCode = "200", 
      description = "Points retrieved successfully",
      content = @Content(schema = @Schema(implementation = PointsAwardedResponse.class))
  )
  @ApiResponse(responseCode = "404", description = "Receipt not found")
  @GetMapping("/{id}/points")
  public ResponseEntity<PointsAwardedResponse> getPoints(
          @PathVariable(name = "id") @NotBlank(message = "receipt id is invalid or missing") String receiptId
  ) throws ResourceNotFoundException {
    ReceiptPoints receiptPoints = receiptProcessorService.getPoints(receiptId);
    PointsAwardedResponse response = new PointsAwardedResponse();
    response.setPoints(receiptPoints.getPoints());
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
