package lib.yyggee.jupiter.validation;

import jakarta.validation.ConstraintViolation;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

  private final jakarta.validation.Validator beanValidator;

  public ValidationService(jakarta.validation.Validator beanValidator) {
    this.beanValidator = beanValidator;
  }

  public <T> ValidationResult validate(T object) {
    Set<ConstraintViolation<T>> violations = beanValidator.validate(object);
    if (violations.isEmpty()) {
      return ValidationResult.valid();
    }

    ValidationResult result = ValidationResult.valid();
    for (ConstraintViolation<T> violation : violations) {
      result.addError(violation.getPropertyPath().toString(), violation.getMessage());
    }
    return result;
  }

  public <T> ValidationResult validate(T object, Class<?>... groups) {
    Set<ConstraintViolation<T>> violations = beanValidator.validate(object, groups);
    if (violations.isEmpty()) {
      return ValidationResult.valid();
    }

    ValidationResult result = ValidationResult.valid();
    for (ConstraintViolation<T> violation : violations) {
      result.addError(violation.getPropertyPath().toString(), violation.getMessage());
    }
    return result;
  }

  public <T> void validateAndThrow(T object) throws ValidationException {
    ValidationResult result = validate(object);
    if (result.isInvalid()) {
      throw new ValidationException(result);
    }
  }

  public <T> void validateAndThrow(T object, Class<?>... groups) throws ValidationException {
    ValidationResult result = validate(object, groups);
    if (result.isInvalid()) {
      throw new ValidationException(result);
    }
  }
}
