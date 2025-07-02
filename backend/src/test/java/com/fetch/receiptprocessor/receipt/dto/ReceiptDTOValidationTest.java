package com.fetch.receiptprocessor.receipt.dto;

import com.fetch.receiptprocessor.receipt.dto.ReceiptDTO;
import com.fetch.receiptprocessor.receipt.dto.ReceiptItemDTO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReceiptDTOValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  private ReceiptDTO buildDto(
          String retailer,
          LocalDate date,
          LocalTime time,
          List<ReceiptItemDTO> items
  ) {
    ReceiptDTO dto = new ReceiptDTO();
    dto.setRetailer(retailer);
    dto.setPurchaseDate(date);
    dto.setPurchaseTime(time);
    dto.setItems(items);
    return dto;
  }

  @Test
  void whenAllFieldsValid_thenNoViolations() {
    ReceiptItemDTO validItem = new ReceiptItemDTO("ValidDesc", "12.34");
    ReceiptDTO dto = buildDto(
            "My-Shop & Co",
            LocalDate.now(),
            LocalTime.now(),
            List.of(validItem)
    );

    Set<ConstraintViolation<ReceiptDTO>> violations = validator.validate(dto);

    assertTrue(violations.isEmpty(),
            () -> "Expected no violations, but got: " + violations);
  }

  @Test
  void whenRetailerBlank_thenPatternViolation() {
    ReceiptItemDTO item = new ReceiptItemDTO("X", "1.00");
    ReceiptDTO dto = buildDto(
            "",
            LocalDate.now(),
            LocalTime.now(),
            List.of(item)
    );

    Set<ConstraintViolation<ReceiptDTO>> violations = validator.validate(dto);

    assertEquals(1, violations.size());
    ConstraintViolation<ReceiptDTO> v = violations.iterator().next();
    assertEquals("The name of the retailer is invalid.", v.getMessage());
    assertEquals("retailer", v.getPropertyPath().toString());
  }

  @Test
  void whenRetailerHasInvalidChars_thenPatternViolation() {
    ReceiptItemDTO item = new ReceiptItemDTO("X", "1.00");
    ReceiptDTO dto = buildDto(
            "Bad@Name!",
            LocalDate.now(),
            LocalTime.now(),
            List.of(item)
    );

    Set<ConstraintViolation<ReceiptDTO>> violations = validator.validate(dto);

    assertEquals(1, violations.size());
    ConstraintViolation<ReceiptDTO> v = violations.iterator().next();
    assertEquals("The name of the retailer is invalid.", v.getMessage());
    assertEquals("retailer", v.getPropertyPath().toString());
  }

  @Test
  void whenPurchaseDateNull_thenNotNullViolation() {
    ReceiptItemDTO item = new ReceiptItemDTO("X", "1.00");
    ReceiptDTO dto = buildDto(
            "Shop",
            null,
            LocalTime.now(),
            List.of(item)
    );

    Set<ConstraintViolation<ReceiptDTO>> violations = validator.validate(dto);

    assertEquals(1, violations.size());
    ConstraintViolation<ReceiptDTO> v = violations.iterator().next();
    assertEquals("purchase date is missing.", v.getMessage());
    assertEquals("purchaseDate", v.getPropertyPath().toString());
  }

  @Test
  void whenPurchaseTimeNull_thenNotNullViolation() {
    ReceiptItemDTO item = new ReceiptItemDTO("X", "1.00");
    ReceiptDTO dto = buildDto(
            "Shop",
            LocalDate.now(),
            null,
            List.of(item)
    );

    Set<ConstraintViolation<ReceiptDTO>> violations = validator.validate(dto);

    assertEquals(1, violations.size());
    ConstraintViolation<ReceiptDTO> v = violations.iterator().next();
    assertEquals("purchase time is missing.", v.getMessage());
    assertEquals("purchaseTime", v.getPropertyPath().toString());
  }

  @Test
  void whenItemsEmpty_thenSizeViolation() {
    ReceiptDTO dto = buildDto(
            "Shop",
            LocalDate.now(),
            LocalTime.now(),
            Collections.emptyList()
    );

    Set<ConstraintViolation<ReceiptDTO>> violations = validator.validate(dto);

    assertEquals(1, violations.size());
    ConstraintViolation<ReceiptDTO> v = violations.iterator().next();
    assertEquals("minimum number of items in a receipt should be 1.", v.getMessage());
    assertEquals("items", v.getPropertyPath().toString());
  }

  @Test
  void whenItemsContainInvalidItem_thenNestedViolation() {
    ReceiptItemDTO badItem = new ReceiptItemDTO("", "abc");
    ReceiptDTO dto = buildDto(
            "Shop",
            LocalDate.now(),
            LocalTime.now(),
            List.of(badItem)
    );

    Set<ConstraintViolation<ReceiptDTO>> violations = validator.validate(dto);

    assertEquals(2, violations.size());
    Set<String> paths = violations.stream()
            .map(v -> v.getPropertyPath().toString())
            .collect(Collectors.toSet());

    assertTrue(paths.contains("items[0].shortDescription"));
    assertTrue(paths.contains("items[0].price"));
  }
}
