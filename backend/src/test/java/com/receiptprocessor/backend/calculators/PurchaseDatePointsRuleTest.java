package com.receiptprocessor.backend.calculators;

import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.service.calculators.PurchaseDatePointsRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PurchaseDatePointsRuleTest {

  private PurchaseDatePointsRule rule;
  private Receipt receipt;

  @BeforeEach
  void setUp() {
    rule = new PurchaseDatePointsRule();
    receipt = new Receipt();
    receipt.setRetailer("Test Store");
    receipt.setPurchaseDateTime(LocalDateTime.now());;
    receipt.setTotal(new BigDecimal("10.00"));
    receipt.setReceiptItems(new ArrayList<>());
  }

  @Test
  void shouldReturn6PointsWhenPurchaseDateIsSunday() {
    receipt.setPurchaseDateTime(LocalDateTime.of(LocalDate.of(2023, 4, 23), LocalTime.now()));
    int points = rule.apply(receipt, false);
    assertEquals(6, points);
  }

  @Test
  void shouldReturn0PointsWhenPurchaseDateIsSaturday() {
    receipt.setPurchaseDateTime(LocalDateTime.of(LocalDate.of(2023, 4, 22), LocalTime.now()));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn0PointsWhenPurchaseDateIsEven() {
    receipt.setPurchaseDateTime(LocalDateTime.of(LocalDate.of(2023, 4, 26), LocalTime.now()));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn6PointsWhenPurchaseDateIsOdd() {
    receipt.setPurchaseDateTime(LocalDateTime.of(LocalDate.of(2023, 4, 11), LocalTime.now()));
    int points = rule.apply(receipt, false);
    assertEquals(6, points);
  }

}
