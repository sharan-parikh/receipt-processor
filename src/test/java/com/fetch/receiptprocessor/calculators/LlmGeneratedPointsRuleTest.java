package com.fetch.receiptprocessor.calculators;

import com.fetch.receiptprocessor.model.Receipt;
import com.fetch.receiptprocessor.service.calculators.LlmGeneratedPointsRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LlmGeneratedPointsRuleTest {

  private LlmGeneratedPointsRule rule;
  private Receipt receipt;

  @BeforeEach
  void setUp() {
    rule = new LlmGeneratedPointsRule();
    receipt = new Receipt();
    receipt.setRetailer("Test Store");
    receipt.setPurchaseDate(LocalDate.now());
    receipt.setPurchaseTime(LocalTime.now());
    receipt.setReceiptItems(new ArrayList<>());
  }

  @Test
  void shouldReturn5PointsWhenLlmGeneratedAndTotalGreaterThan10() {
    receipt.setTotal(new BigDecimal("10.01"));
    boolean llmGenerated = true;
    int points = rule.apply(receipt, llmGenerated);
    assertEquals(5, points);
  }

  @Test
  void shouldReturn0PointsWhenNotLlmGeneratedAndTotalGreaterThan10() {
    receipt.setTotal(new BigDecimal("10.01"));
    boolean llmGenerated = false;
    int points = rule.apply(receipt, llmGenerated);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn0PointsWhenLlmGeneratedAndTotalExactly10() {
    receipt.setTotal(new BigDecimal("10.00"));
    boolean llmGenerated = true;
    int points = rule.apply(receipt, llmGenerated);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn0PointsWhenLlmGeneratedAndTotalLessThan10() {
    receipt.setTotal(new BigDecimal("9.99"));
    boolean llmGenerated = true;
    int points = rule.apply(receipt, llmGenerated);
    assertEquals(0, points);
  }
}