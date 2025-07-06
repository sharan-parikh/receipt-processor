package com.receiptprocessor.backend.calculators;

import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.service.calculators.RetailerPointsRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RetailerPointsRuleTest {

  private RetailerPointsRule rule;
  private Receipt receipt;

  @BeforeEach
  void setUp() {
    rule = new RetailerPointsRule();
    receipt = new Receipt();
    receipt.setPurchaseDateTime(LocalDateTime.now());
    receipt.setTotal(new BigDecimal("10.00"));
    receipt.setReceiptItems(new ArrayList<>());
  }

  @Test
  void shouldReturnPointsEqualToNumberOfAlphaNumericChars() {
    receipt.setRetailer("Target");
    int points = rule.apply(receipt, false);
    assertEquals(6, points);
  }

  @Test
  void shouldReturnZeroPointsWhenRetailerNameIsEmpty() {
    receipt.setRetailer("");
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldNotCountSpacesInRetailerName() {
    receipt.setRetailer("Walmart Supercenter");
    int points = rule.apply(receipt, false);
    assertEquals(18, points);
  }

  @Test
  void shouldHandleSpecialCharactersInRetailerName() {
    receipt.setRetailer("Ben & Jerry's");
    int points = rule.apply(receipt, false);
    assertEquals(9, points);
  }

  @Test
  void shouldHandleNullRetailerName() {
    receipt.setRetailer(null);
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }
}