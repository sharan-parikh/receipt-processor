package com.receiptprocessor.backend.calculators;

import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.model.ReceiptItem;
import com.receiptprocessor.backend.receipt.service.calculators.ItemDescriptionPointsRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemDescriptionPointsRuleTest {

  private ItemDescriptionPointsRule rule;
  private Receipt receipt;

  @BeforeEach
  void setUp() {
    rule = new ItemDescriptionPointsRule();
    receipt = new Receipt();
    receipt.setRetailer("Test Store");
    receipt.setPurchaseDateTime(LocalDateTime.now());
    receipt.setTotal(new BigDecimal("10.00"));
    receipt.setReceiptItems(new ArrayList<>());
  }

  @Test
  void shouldReturn0PointsWhenNoItems() {
    receipt.setReceiptItems(new ArrayList<>());
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldCalculatePointsCorrectlyWhenDescriptionLengthDivisibleBy3() {
    ReceiptItem item1 = new ReceiptItem();
    item1.setShortDescription("ABC");
    item1.setPrice(new BigDecimal("10.00"));

    ReceiptItem item2 = new ReceiptItem();
    item2.setShortDescription("DEFGHI");
    item2.setPrice(new BigDecimal("5.00"));

    receipt.setReceiptItems(Arrays.asList(item1, item2));

    int points = rule.apply(receipt, false);
    assertEquals(3, points);
  }

  @Test
  void shouldIgnoreItemsWhenDescriptionLengthNotDivisibleBy3() {
    ReceiptItem item1 = new ReceiptItem();
    item1.setShortDescription("ABCD");
    item1.setPrice(new BigDecimal("10.00"));

    ReceiptItem item2 = new ReceiptItem();
    item2.setShortDescription("ABC");
    item2.setPrice(new BigDecimal("5.00"));

    receipt.setReceiptItems(Arrays.asList(item1, item2));

    int points = rule.apply(receipt, false);
    assertEquals(1, points);
  }

  @Test
  void shouldRoundPointsUpCorrectly() {
    ReceiptItem item = new ReceiptItem();
    item.setShortDescription("ABC");
    item.setPrice(new BigDecimal("4.99"));

    receipt.setReceiptItems(Arrays.asList(item));

    int points = rule.apply(receipt, false);
    assertEquals(1, points);
  }

  @Test
  void shouldHandleTrimmedDescriptionLength() {
    ReceiptItem item1 = new ReceiptItem();
    item1.setShortDescription(" ABC ");
    item1.setPrice(new BigDecimal("10.00"));

    receipt.setReceiptItems(Arrays.asList(item1));

    int points = rule.apply(receipt, false);
    assertEquals(2, points);
  }

  @Test
  void shouldReturnZeroWhenAllItemDescriptionsAreNotDivisibleBy3() {
    ReceiptItem item1 = new ReceiptItem();
    item1.setShortDescription("AB");
    item1.setPrice(new BigDecimal("10.00"));

    ReceiptItem item2 = new ReceiptItem();
    item2.setShortDescription("ABCD");
    item2.setPrice(new BigDecimal("5.00"));

    receipt.setReceiptItems(Arrays.asList(item1, item2));

    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }
}