package com.fetch.receiptprocessor.unit.exception;

import com.fetch.receiptprocessor.exception.GlobalExceptionHandler;
import com.fetch.receiptprocessor.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

  @InjectMocks
  private GlobalExceptionHandler globalExceptionHandler;

  @Mock
  private MethodArgumentNotValidException methodArgumentNotValidException;

  @Mock
  private BindingResult bindingResult;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void handleValidationExceptions_ShouldReturnFieldErrors() {
    FieldError fieldError = new FieldError("objectName", "fieldName", "error message");
    List<FieldError> fieldErrors = Collections.singletonList(fieldError);

    when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

    ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    assertEquals("error message", response.getBody().get("fieldName"));
  }

  @Test
  void handleValidationExceptions_WithMultipleErrors_ShouldReturnAllErrors() {
    FieldError fieldError1 = new FieldError("objectName", "field1", "error message 1");
    FieldError fieldError2 = new FieldError("objectName", "field2", "error message 2");
    List<FieldError> fieldErrors = List.of(fieldError1, fieldError2);

    when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

    ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(2, response.getBody().size());
    assertEquals("error message 1", response.getBody().get("field1"));
    assertEquals("error message 2", response.getBody().get("field2"));
  }

  @Test
  void handleResourceNotFoundException_ShouldReturnNotFoundWithMessage() {
    ResourceNotFoundException exception = new ResourceNotFoundException("Receipt not found");

    ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleReceiptNotFoundException(exception);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    assertEquals("Receipt not found", response.getBody().get("message"));
  }

  @Test
  void handleGeneralExceptions_ShouldReturnInternalServerError() {
    Exception exception = new RuntimeException("Some unexpected error");

    ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleGeneralExceptions(exception);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    assertEquals("An error occurred", response.getBody().get("message"));
  }

  @Test
  void handleValidationExceptions_WithNoErrors_ShouldReturnEmptyMap() {
    List<FieldError> fieldErrors = Collections.emptyList();

    when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

    ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(methodArgumentNotValidException);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertTrue(response.getBody().isEmpty());
  }

  @Test
  void handleConstraintValidationExceptions_ShouldReturnBadRequest() {
    ConstraintViolation<Object> v1 = mock(ConstraintViolation.class);
    Path path = mock(Path.class);
    when(path.toString()).thenReturn("field two");
    when(v1.getMessage()).thenReturn("invalid format");

    ConstraintViolationException ex = new ConstraintViolationException(Set.of(v1));

    ResponseEntity<?> response = globalExceptionHandler.onConstraintViolation(ex);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(1, ((Map<String, List<String>>)response.getBody()).size());
  }
}
