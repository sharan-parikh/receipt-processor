package com.fetch.receiptprocessor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/receipts")
public class ReceiptProcessorController {

  @PostMapping("/process")
  public void processReceipt() {

  }

  @GetMapping("/{id}/points")
  public void processReceipt(@PathVariable(name = "id") @NotEmpty String receiptId) {

  }
}
