package lib.yyggee.jupiter.validation;

import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Validators {

  private Validators() {}

  public static <T> Validator<T> notNull(String field, Function<T, Object> getter) {
    return target -> {
      Object value = getter.apply(target);
      if (value == null) {
        return ValidationResult.invalid(field, "must not be null");
      }
      return ValidationResult.valid();
    };
  }

  public static <T> Validator<T> notEmpty(String field, Function<T, String> getter) {
    return target -> {
      String value = getter.apply(target);
      if (value == null || value.trim().isEmpty()) {
        return ValidationResult.invalid(field, "must not be empty");
      }
      return ValidationResult.valid();
    };
  }

  public static <T> Validator<T> notEmptyCollection(
      String field, Function<T, Collection<?>> getter) {
    return target -> {
      Collection<?> value = getter.apply(target);
      if (value == null || value.isEmpty()) {
        return ValidationResult.invalid(field, "must not be empty");
      }
      return ValidationResult.valid();
    };
  }

  public static <T> Validator<T> minLength(String field, Function<T, String> getter, int min) {
    return target -> {
      String value = getter.apply(target);
      if (value != null && value.length() < min) {
        return ValidationResult.invalid(field, "must be at least " + min + " characters");
      }
      return ValidationResult.valid();
    };
  }

  public static <T> Validator<T> maxLength(String field, Function<T, String> getter, int max) {
    return target -> {
      String value = getter.apply(target);
      if (value != null && value.length() > max) {
        return ValidationResult.invalid(field, "must be at most " + max + " characters");
      }
      return ValidationResult.valid();
    };
  }

  public static <T> Validator<T> length(
      String field, Function<T, String> getter, int min, int max) {
    return minLength(field, getter, min).and(maxLength(field, getter, max));
  }

  public static <T> Validator<T> pattern(
      String field, Function<T, String> getter, String regex, String message) {
    Pattern compiledPattern = Pattern.compile(regex);
    return target -> {
      String value = getter.apply(target);
      if (value != null && !compiledPattern.matcher(value).matches()) {
        return ValidationResult.invalid(field, message);
      }
      return ValidationResult.valid();
    };
  }

  public static <T> Validator<T> email(String field, Function<T, String> getter) {
    return pattern(
        field,
        getter,
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        "must be a valid email address");
  }

  public static <T, N extends Number & Comparable<N>> Validator<T> min(
      String field, Function<T, N> getter, N min) {
    return target -> {
      N value = getter.apply(target);
      if (value != null && value.compareTo(min) < 0) {
        return ValidationResult.invalid(field, "must be greater than or equal to " + min);
      }
      return ValidationResult.valid();
    };
  }

  public static <T, N extends Number & Comparable<N>> Validator<T> max(
      String field, Function<T, N> getter, N max) {
    return target -> {
      N value = getter.apply(target);
      if (value != null && value.compareTo(max) > 0) {
        return ValidationResult.invalid(field, "must be less than or equal to " + max);
      }
      return ValidationResult.valid();
    };
  }

  public static <T, N extends Number & Comparable<N>> Validator<T> range(
      String field, Function<T, N> getter, N min, N max) {
    return min(field, getter, min).and(max(field, getter, max));
  }

  public static <T> Validator<T> positive(String field, Function<T, Number> getter) {
    return target -> {
      Number value = getter.apply(target);
      if (value != null && value.doubleValue() <= 0) {
        return ValidationResult.invalid(field, "must be positive");
      }
      return ValidationResult.valid();
    };
  }

  public static <T> Validator<T> positiveOrZero(String field, Function<T, Number> getter) {
    return target -> {
      Number value = getter.apply(target);
      if (value != null && value.doubleValue() < 0) {
        return ValidationResult.invalid(field, "must be positive or zero");
      }
      return ValidationResult.valid();
    };
  }

  @SafeVarargs
  public static <T> Validator<T> compose(Validator<T>... validators) {
    return target -> {
      ValidationResult result = ValidationResult.valid();
      for (Validator<T> validator : validators) {
        result.merge(validator.validate(target));
      }
      return result;
    };
  }
}
