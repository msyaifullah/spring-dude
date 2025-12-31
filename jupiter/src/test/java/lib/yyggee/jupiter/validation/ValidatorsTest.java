package lib.yyggee.jupiter.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

class ValidatorsTest {

  @Data
  @AllArgsConstructor
  static class TestDto {
    private String name;
    private String email;
    private Integer age;
  }

  @Test
  void notNull_valid() {
    TestDto dto = new TestDto("John", "john@example.com", 25);
    Validator<TestDto> validator = Validators.notNull("name", TestDto::getName);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isValid(), is(true));
  }

  @Test
  void notNull_invalid() {
    TestDto dto = new TestDto(null, "john@example.com", 25);
    Validator<TestDto> validator = Validators.notNull("name", TestDto::getName);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isInvalid(), is(true));
  }

  @Test
  void notEmpty_valid() {
    TestDto dto = new TestDto("John", "john@example.com", 25);
    Validator<TestDto> validator = Validators.notEmpty("name", TestDto::getName);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isValid(), is(true));
  }

  @Test
  void notEmpty_invalidWhenNull() {
    TestDto dto = new TestDto(null, "john@example.com", 25);
    Validator<TestDto> validator = Validators.notEmpty("name", TestDto::getName);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isInvalid(), is(true));
  }

  @Test
  void notEmpty_invalidWhenEmpty() {
    TestDto dto = new TestDto("  ", "john@example.com", 25);
    Validator<TestDto> validator = Validators.notEmpty("name", TestDto::getName);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isInvalid(), is(true));
  }

  @Test
  void minLength_valid() {
    TestDto dto = new TestDto("John", "john@example.com", 25);
    Validator<TestDto> validator = Validators.minLength("name", TestDto::getName, 3);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isValid(), is(true));
  }

  @Test
  void minLength_invalid() {
    TestDto dto = new TestDto("Jo", "john@example.com", 25);
    Validator<TestDto> validator = Validators.minLength("name", TestDto::getName, 3);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isInvalid(), is(true));
  }

  @Test
  void email_valid() {
    TestDto dto = new TestDto("John", "john@example.com", 25);
    Validator<TestDto> validator = Validators.email("email", TestDto::getEmail);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isValid(), is(true));
  }

  @Test
  void email_invalid() {
    TestDto dto = new TestDto("John", "invalid-email", 25);
    Validator<TestDto> validator = Validators.email("email", TestDto::getEmail);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isInvalid(), is(true));
  }

  @Test
  void min_valid() {
    TestDto dto = new TestDto("John", "john@example.com", 25);
    Validator<TestDto> validator = Validators.min("age", TestDto::getAge, 18);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isValid(), is(true));
  }

  @Test
  void min_invalid() {
    TestDto dto = new TestDto("John", "john@example.com", 15);
    Validator<TestDto> validator = Validators.min("age", TestDto::getAge, 18);

    ValidationResult result = validator.validate(dto);

    assertThat(result.isInvalid(), is(true));
  }

  @Test
  void compose_combinesValidators() {
    TestDto dto = new TestDto("Jo", "invalid", 15);
    Validator<TestDto> validator =
        Validators.compose(
            Validators.minLength("name", TestDto::getName, 3),
            Validators.email("email", TestDto::getEmail),
            Validators.min("age", TestDto::getAge, 18));

    ValidationResult result = validator.validate(dto);

    assertThat(result.isInvalid(), is(true));
    assertThat(result.getErrors().size(), is(3));
  }

  @Test
  void and_chainsValidators() {
    TestDto dto = new TestDto("Jo", "john@example.com", 25);
    Validator<TestDto> validator =
        Validators.notEmpty("name", TestDto::getName)
            .and(Validators.minLength("name", TestDto::getName, 3));

    ValidationResult result = validator.validate(dto);

    assertThat(result.isInvalid(), is(true));
    assertThat(result.getErrors().size(), is(1));
  }
}
