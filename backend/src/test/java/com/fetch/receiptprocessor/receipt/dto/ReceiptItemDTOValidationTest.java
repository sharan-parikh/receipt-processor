package com.fetch.receiptprocessor.receipt.dto;

import com.fetch.receiptprocessor.receipt.dto.ReceiptItemDTO;

import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReceiptItemDTOValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  void whenBothFieldsValid_thenNoViolations() {
    // Arrange
    ReceiptItemDTO dto = new ReceiptItemDTO("Valid Description", "12.34");

    // Act
    Set<ConstraintViolation<ReceiptItemDTO>> violations = validator.validate(dto);

    // Assert
    assertTrue(violations.isEmpty(),
            () -> "Expected no violations, but got: " + violations);
  }

  @Test
  void whenShortDescriptionHasInvalidChars_thenPatternViolation() {
    // Arrange
    ReceiptItemDTO dto = new ReceiptItemDTO("Bad@Desc!", "12.34");

    // Act
    Set<ConstraintViolation<ReceiptItemDTO>> violations = validator.validate(dto);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<ReceiptItemDTO> v = violations.iterator().next();
    assertEquals("short description of an item is invalid", v.getMessage());
    assertEquals("shortDescription", v.getPropertyPath().toString());
  }

  @Test
  void whenShortDescriptionBlank_thenPatternViolation() {
    // Arrange
    ReceiptItemDTO dto = new ReceiptItemDTO("", "12.34");

    // Act
    Set<ConstraintViolation<ReceiptItemDTO>> violations = validator.validate(dto);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<ReceiptItemDTO> v = violations.iterator().next();
    assertEquals("short description of an item is invalid", v.getMessage());
    assertEquals("shortDescription", v.getPropertyPath().toString());
  }

  @Test
  void whenPriceWrongFormat_thenPatternViolation() {
    // Arrange
    ReceiptItemDTO dto = new ReceiptItemDTO("Valid", "12.3");  // only one decimal

    // Act
    Set<ConstraintViolation<ReceiptItemDTO>> violations = validator.validate(dto);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<ReceiptItemDTO> v = violations.iterator().next();
    assertEquals("price of an item is invalid", v.getMessage());
    assertEquals("price", v.getPropertyPath().toString());
  }

  @Test
  void whenPriceBlank_thenPatternViolation() {
    // Arrange
    ReceiptItemDTO dto = new ReceiptItemDTO("Valid", "");

    // Act
    Set<ConstraintViolation<ReceiptItemDTO>> violations = validator.validate(dto);

    // Assert
    assertEquals(1, violations.size());
    ConstraintViolation<ReceiptItemDTO> v = violations.iterator().next();
    assertEquals("price of an item is invalid", v.getMessage());
    assertEquals("price", v.getPropertyPath().toString());
  }

  @Test
  void whenBothFieldsInvalid_thenTwoViolations() {
    // Arrange
    ReceiptItemDTO dto = new ReceiptItemDTO("", "abc");

    // Act
    Set<ConstraintViolation<ReceiptItemDTO>> violations = validator.validate(dto);

    // Assert
    assertEquals(2, violations.size());
    Set<String> messages = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toSet());

    assertTrue(messages.contains("short description of an item is invalid"));
    assertTrue(messages.contains("price of an item is invalid"));
  }
}
