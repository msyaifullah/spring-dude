package lib.yyggee.jupiter.validation;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {

  private final ValidationResult validationResult;

  public ValidationException(ValidationResult validationResult) {
    super(validationResult.getErrorMessages());
    this.validationResult = validationResult;
  }

  public ValidationException(String field, String message) {
    super(field + ": " + message);
    this.validationResult = ValidationResult.invalid(field, message);
  }
}
