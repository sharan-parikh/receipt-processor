package com.fetch.receiptprocessor.unit.service.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.calculators.QuarterMultipleRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuarterMultipleRuleTest {

  private QuarterMultipleRule rule;
  private Receipt receipt;

  @BeforeEach
  void setUp() {
    rule = new QuarterMultipleRule();
    receipt = new Receipt();
    receipt.setRetailer("Test Store");
    receipt.setPurchaseDate(LocalDate.now());
    receipt.setPurchaseTime(LocalTime.now());
    receipt.setReceiptItems(new ArrayList<>());
  }

  @Test
  void shouldReturn25PointsWhenTotalIsExactMultipleOfQuarter() {
    receipt.setTotal(new BigDecimal("10.25"));
    int points = rule.apply(receipt, false);
    assertEquals(25, points);
  }

  @Test
  void shouldReturn0PointsWhenTotalIsNotMultipleOfQuarter() {
    receipt.setTotal(new BigDecimal("10.26"));
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn25PointsWhenTotalIsWholeNumber() {
    receipt.setTotal(new BigDecimal("10.00"));
    int points = rule.apply(receipt, false);
    assertEquals(25, points);
  }

  @Test
  void shouldReturn25PointsWhenTotalHas50Cents() {
    receipt.setTotal(new BigDecimal("10.50"));
    int points = rule.apply(receipt, false);
    assertEquals(25, points);
  }

  @Test
  void shouldReturn25PointsWhenTotalHas75Cents() {
    receipt.setTotal(new BigDecimal("10.75"));
    int points = rule.apply(receipt, false);
    assertEquals(25, points);
  }
}
