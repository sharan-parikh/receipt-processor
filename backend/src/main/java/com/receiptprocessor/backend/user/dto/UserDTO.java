package com.receiptprocessor.backend.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserDTO {

  @Email
  String email;

}
