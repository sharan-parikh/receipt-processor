package com.receiptprocessor.backend.calculators;

import com.receiptprocessor.backend.receipt.model.Receipt;
import com.receiptprocessor.backend.receipt.model.ReceiptItem;
import com.receiptprocessor.backend.receipt.service.calculators.ItemCountPointsRule;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemCountPointsRuleTest {

  private ItemCountPointsRule rule;
  private Receipt receipt;

  @BeforeEach
  void setUp() {
    rule = new ItemCountPointsRule();
    receipt = new Receipt();
    receipt.setRetailer("Test Store");
    receipt.setPurchaseDateTime(LocalDateTime.now());
    receipt.setTotal(new BigDecimal("10.00"));
  }

  @Test
  void shouldReturn0PointsWhenNoItems() {
    receipt.setReceiptItems(new ArrayList<>());
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn0PointsWhenOneItem() {
    List<ReceiptItem> items = new ArrayList<>();
    items.add(new ReceiptItem());
    receipt.setReceiptItems(items);
    int points = rule.apply(receipt, false);
    assertEquals(0, points);
  }

  @Test
  void shouldReturn5PointsWhenTwoItems() {
    List<ReceiptItem> items = new ArrayList<>();
    items.add(new ReceiptItem());
    items.add(new ReceiptItem());
    receipt.setReceiptItems(items);
    int points = rule.apply(receipt, false);
    assertEquals(5, points);
  }

  @Test
  void shouldReturn5PointsWhenThreeItems() {
    List<ReceiptItem> items = new ArrayList<>();
    items.add(new ReceiptItem());
    items.add(new ReceiptItem());
    items.add(new ReceiptItem());
    receipt.setReceiptItems(items);
    int points = rule.apply(receipt, false);
    assertEquals(5, points);
  }

  @Test
  void shouldReturn10PointsWhenFourItems() {
    List<ReceiptItem> items = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      items.add(new ReceiptItem());
    }
    receipt.setReceiptItems(items);
    int points = rule.apply(receipt, false);
    assertEquals(10, points);
  }

  @Test
  void shouldReturn10PointsWhenFiveItems() {
    List<ReceiptItem> items = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      items.add(new ReceiptItem());
    }
    receipt.setReceiptItems(items);
    int points = rule.apply(receipt, false);
    assertEquals(10, points);
  }

  @Test
  void shouldReturn15PointsWhenSixItems() {
    List<ReceiptItem> items = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      items.add(new ReceiptItem());
    }
    receipt.setReceiptItems(items);
    int points = rule.apply(receipt, false);
    assertEquals(15, points);
  }
}
