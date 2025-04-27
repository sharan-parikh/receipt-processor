package com.fetch.receiptprocessor.unit.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.calculators.RoundDollarPointsRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoundDollarPointsRuleTest {

  private RoundDollarPointsRule rule;
  private Receipt receipt;

  @BeforeEach
  void setUp() {
    rule = new RoundDollarPointsRule();
    receipt = new Receipt();
    receipt.setRetailer("Test Store");
    receipt.setPurchaseDate(LocalDate.now());
    receipt.setPurchaseTime(LocalTime.now());
    receipt.setReceiptItems(new ArrayList<>());
  }

  @Test
  void shouldReturn50PointsWhenTotalIsWholeNumber() {
    receipt.setTotal(new BigDecimal("100.00"));
    int points = rule.apply(receipt, false);
    assertEquals(50, points);
  }

  @Test
  void shouldReturn0PointsWhenTotalHasCents() {
    receipt.setTotal(new BigDecimal("100.01"));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn50PointsWhenTotalIsZero() {
    receipt.setTotal(new BigDecimal("0.00"));
    int points = rule.apply(receipt, false);
    assertEquals(50, points);
  }

  @Test
  void shouldReturn0PointsWhenTotalHasManyDecimalPlaces() {
    receipt.setTotal(new BigDecimal("100.001"));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }
}
