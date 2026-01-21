package lib.yyggee.jupiter.validation;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.function.Function;

public class ValidationRule<T> {

  private final String name;
  private final Validator<T> validator;
  private final FailMode failMode;

  private ValidationRule(String name, Validator<T> validator, FailMode failMode) {
    this.name = name;
    this.validator = validator;
    this.failMode = failMode;
  }

  public static <T> ValidationRule<T> hard(String name, Validator<T> validator) {
    return new ValidationRule<>(name, validator, FailMode.HARD);
  }

  public static <T> ValidationRule<T> soft(String name, Validator<T> validator) {
    return new ValidationRule<>(name, validator, FailMode.SOFT);
  }

  public static <T> Builder<T> builder(String name) {
    return new Builder<>(name);
  }

  public String getName() {
    return name;
  }

  public FailMode getFailMode() {
    return failMode;
  }

  public ValidationRuleResult execute(T target) {
    Instant start = Instant.now();
    try {
      ValidationResult result = validator.validate(target);
      Duration duration = Duration.between(start, Instant.now());

      return ValidationRuleResult.builder()
          .ruleName(name)
          .passed(result.isValid())
          .failMode(failMode)
          .errors(result.getErrors())
          .executionTime(duration)
          .build();
    } catch (Exception e) {
      Duration duration = Duration.between(start, Instant.now());
      return ValidationRuleResult.builder()
          .ruleName(name)
          .passed(false)
          .failMode(failMode)
          .errors(Collections.emptyList())
          .executionTime(duration)
          .exception(e)
          .build();
    }
  }

  public static class Builder<T> {
    private final String name;
    private Validator<T> validator;
    private FailMode failMode = FailMode.HARD;

    private Builder(String name) {
      this.name = name;
    }

    public Builder<T> validator(Validator<T> validator) {
      this.validator = validator;
      return this;
    }

    public Builder<T> check(
        String field,
        Function<T, Object> getter,
        Function<Object, Boolean> predicate,
        String message) {
      this.validator =
          target -> {
            Object value = getter.apply(target);
            if (!predicate.apply(value)) {
              return ValidationResult.invalid(field, message);
            }
            return ValidationResult.valid();
          };
      return this;
    }

    public Builder<T> failMode(FailMode failMode) {
      this.failMode = failMode;
      return this;
    }

    public Builder<T> hard() {
      this.failMode = FailMode.HARD;
      return this;
    }

    public Builder<T> soft() {
      this.failMode = FailMode.SOFT;
      return this;
    }

    public ValidationRule<T> build() {
      if (validator == null) {
        throw new IllegalStateException("Validator must be set");
      }
      return new ValidationRule<>(name, validator, failMode);
    }
  }
}
