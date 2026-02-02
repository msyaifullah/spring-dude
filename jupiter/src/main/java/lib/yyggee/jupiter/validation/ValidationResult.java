package lib.yyggee.jupiter.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class ValidationResult {

  private final List<ValidationError> errors;

  private ValidationResult() {
    this.errors = new ArrayList<>();
  }

  public static ValidationResult valid() {
    return new ValidationResult();
  }

  public static ValidationResult invalid(String field, String message) {
    ValidationResult result = new ValidationResult();
    result.addError(field, message);
    return result;
  }

  public static ValidationResult invalid(List<ValidationError> errors) {
    ValidationResult result = new ValidationResult();
    result.errors.addAll(errors);
    return result;
  }

  public boolean isValid() {
    return errors.isEmpty();
  }

  public boolean isInvalid() {
    return !errors.isEmpty();
  }

  public void addError(String field, String message) {
    errors.add(new ValidationError(field, message));
  }

  public void addError(ValidationError error) {
    errors.add(error);
  }

  public void addErrors(List<ValidationError> errors) {
    this.errors.addAll(errors);
  }

  public ValidationResult merge(ValidationResult other) {
    if (other != null && other.isInvalid()) {
      this.errors.addAll(other.getErrors());
    }
    return this;
  }

  public List<ValidationError> getErrors() {
    return Collections.unmodifiableList(errors);
  }

  public String getErrorMessages() {
    StringBuilder sb = new StringBuilder();
    for (ValidationError error : errors) {
      if (!sb.isEmpty()) {
        sb.append("; ");
      }
      sb.append(error.getField()).append(": ").append(error.getMessage());
    }
    return sb.toString();
  }
}
