package com.fetch.receiptprocessor.unit.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.calculators.RetailerPointsRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RetailerPointsRuleTest {

  private RetailerPointsRule rule;
  private Receipt receipt;

  @BeforeEach
  void setUp() {
    rule = new RetailerPointsRule();
    receipt = new Receipt();
    receipt.setPurchaseDate(LocalDate.now());
    receipt.setPurchaseTime(LocalTime.now());
    receipt.setTotal(new BigDecimal("10.00"));
    receipt.setReceiptItemsIds(new ArrayList<>());
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