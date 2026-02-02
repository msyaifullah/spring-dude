package lib.yyggee.jupiter.validation;

@FunctionalInterface
public interface Validator<T> {

  ValidationResult validate(T target);

  default Validator<T> and(Validator<T> other) {
    return target -> {
      ValidationResult result = this.validate(target);
      return result.merge(other.validate(target));
    };
  }
}
