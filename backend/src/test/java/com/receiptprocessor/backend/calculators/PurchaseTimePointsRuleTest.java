package com.receiptprocessor.backend.calculators;

import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.service.calculators.PurchaseTimePointsRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PurchaseTimePointsRuleTest {

  private PurchaseTimePointsRule rule;
  private Receipt receipt;

  @BeforeEach
  void setUp() {
    rule = new PurchaseTimePointsRule();
    receipt = new Receipt();
    receipt.setRetailer("Test Store");
    receipt.setPurchaseDateTime(LocalDateTime.now());
    receipt.setTotal(new BigDecimal("10.00"));
    receipt.setReceiptItems(new ArrayList<>());
  }

  @Test
  void shouldReturn10PointsWhenPurchaseTimeBetween2And4PM() {
    receipt.setPurchaseDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0)));
    int points = rule.apply(receipt, false);
    assertEquals(10, points);
  }

  @Test
  void shouldReturn0PointsWhenPurchaseTimeIsExactly2PM() {
    receipt.setPurchaseDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(14, 0)));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn0PointsWhenPurchaseTimeIsExactly4PM() {
    receipt.setPurchaseDateTime(LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0)));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn0PointsWhenPurchaseTimeIsBefore2PM() {
    receipt.setPurchaseDateTime(LocalDateTime.of(LocalDate.now(),LocalTime.of(13, 59)));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn0PointsWhenPurchaseTimeIsAfter4PM() {
    receipt.setPurchaseDateTime(LocalDateTime.of(LocalDate.now(),LocalTime.of(16, 1)));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }
}
