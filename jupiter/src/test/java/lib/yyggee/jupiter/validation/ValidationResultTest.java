package lib.yyggee.jupiter.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

class ValidationResultTest {

  @Test
  void valid_shouldBeValid() {
    ValidationResult result = ValidationResult.valid();

    assertThat(result.isValid(), is(true));
    assertThat(result.isInvalid(), is(false));
    assertThat(result.getErrors(), hasSize(0));
  }

  @Test
  void invalid_shouldContainError() {
    ValidationResult result = ValidationResult.invalid("field", "must not be null");

    assertThat(result.isValid(), is(false));
    assertThat(result.isInvalid(), is(true));
    assertThat(result.getErrors(), hasSize(1));
    assertThat(result.getErrors().get(0).getField(), is("field"));
    assertThat(result.getErrors().get(0).getMessage(), is("must not be null"));
  }

  @Test
  void addError_shouldAddToErrors() {
    ValidationResult result = ValidationResult.valid();
    result.addError("name", "is required");
    result.addError("email", "is invalid");

    assertThat(result.isInvalid(), is(true));
    assertThat(result.getErrors(), hasSize(2));
  }

  @Test
  void merge_shouldCombineErrors() {
    ValidationResult result1 = ValidationResult.invalid("field1", "error1");
    ValidationResult result2 = ValidationResult.invalid("field2", "error2");

    result1.merge(result2);

    assertThat(result1.getErrors(), hasSize(2));
  }

  @Test
  void getErrorMessages_shouldFormatErrors() {
    ValidationResult result = ValidationResult.valid();
    result.addError("name", "is required");
    result.addError("email", "is invalid");

    String messages = result.getErrorMessages();

    assertThat(messages, containsString("name: is required"));
    assertThat(messages, containsString("email: is invalid"));
  }
}
