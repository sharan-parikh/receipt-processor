package com.fetch.receiptprocessor.unit.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.calculators.PurchaseDatePointsRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
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
    receipt.setPurchaseTime(LocalTime.now());
    receipt.setTotal(new BigDecimal("10.00"));
    receipt.setReceiptItemsIds(new ArrayList<>());
    receipt.setReceiptItems(new ArrayList<>());
  }

  @Test
  void shouldReturn6PointsWhenPurchaseDateIsSunday() {
    receipt.setPurchaseDate(LocalDate.of(2023, 4, 23));
    int points = rule.apply(receipt, false);
    assertEquals(6, points);
  }

  @Test
  void shouldReturn0PointsWhenPurchaseDateIsSaturday() {
    receipt.setPurchaseDate(LocalDate.of(2023, 4, 22));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn6PointsWhenPurchaseDateIsMonday() {
    receipt.setPurchaseDate(LocalDate.of(2023, 4, 24));
    int points = rule.apply(receipt, false);
    assertEquals(6, points);
  }

  @Test
  void shouldReturn0PointsWhenPurchaseDateIsTuesday() {
    receipt.setPurchaseDate(LocalDate.of(2023, 4, 25));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn6PointsWhenPurchaseDateIsWednesday() {
    receipt.setPurchaseDate(LocalDate.of(2023, 4, 26));
    int points = rule.apply(receipt, false);
    assertEquals(6, points);
  }

  @Test
  void shouldReturn0PointsWhenPurchaseDateIsThursday() {
    receipt.setPurchaseDate(LocalDate.of(2023, 4, 27));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn6PointsWhenPurchaseDateIsFriday() {
    receipt.setPurchaseDate(LocalDate.of(2023, 4, 28));
    int points = rule.apply(receipt, false);
    assertEquals(6, points);
  }
}
