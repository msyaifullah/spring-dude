package lib.yyggee.jupiter.validation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;

class ValidationChainTest {

  @Data
  @AllArgsConstructor
  static class User {
    private String name;
    private String email;
    private Integer age;
  }

  @Test
  void allRulesPass_shouldReturnPassed() {
    User user = new User("John Doe", "john@example.com", 25);

    ValidationChain<User> chain =
        ValidationChain.<User>builder()
            .addHardRule("name-required", Validators.notEmpty("name", User::getName))
            .addHardRule("email-valid", Validators.email("email", User::getEmail))
            .addHardRule("age-valid", Validators.min("age", User::getAge, 18))
            .build();

    ValidationChainResult result = chain.validate(user);

    assertThat(result.isPassed(), is(true));
    assertThat(result.isHasHardFailures(), is(false));
    assertThat(result.isHasSoftFailures(), is(false));
    assertThat(result.getPassedCount(), is(3));
    assertThat(result.getFailedCount(), is(0));
  }

  @Test
  void hardRuleFails_shouldReturnFailed() {
    User user = new User("", "john@example.com", 25);

    ValidationChain<User> chain =
        ValidationChain.<User>builder()
            .addHardRule("name-required", Validators.notEmpty("name", User::getName))
            .addHardRule("email-valid", Validators.email("email", User::getEmail))
            .build();

    ValidationChainResult result = chain.validate(user);

    assertThat(result.isPassed(), is(false));
    assertThat(result.isHasHardFailures(), is(true));
    assertThat(result.getHardFailedCount(), is(1));
    assertThat(result.getHardFailedRules().get(0).getRuleName(), is("name-required"));
  }

  @Test
  void softRuleFails_shouldReturnPassedWithWarnings() {
    User user = new User("John", "invalid-email", 25);

    ValidationChain<User> chain =
        ValidationChain.<User>builder()
            .addHardRule("name-required", Validators.notEmpty("name", User::getName))
            .addSoftRule("email-valid", Validators.email("email", User::getEmail))
            .build();

    ValidationChainResult result = chain.validate(user);

    assertThat(result.isPassed(), is(true));
    assertThat(result.isHasHardFailures(), is(false));
    assertThat(result.isHasSoftFailures(), is(true));
    assertThat(result.getSoftFailedCount(), is(1));
    assertThat(result.getSoftFailedRules().get(0).getRuleName(), is("email-valid"));
  }

  @Test
  void mixedFailures_shouldReportBoth() {
    User user = new User("", "invalid-email", 15);

    ValidationChain<User> chain =
        ValidationChain.<User>builder()
            .addHardRule("name-required", Validators.notEmpty("name", User::getName))
            .addSoftRule("email-valid", Validators.email("email", User::getEmail))
            .addHardRule("age-valid", Validators.min("age", User::getAge, 18))
            .build();

    ValidationChainResult result = chain.validate(user);

    assertThat(result.isPassed(), is(false));
    assertThat(result.isHasHardFailures(), is(true));
    assertThat(result.isHasSoftFailures(), is(true));
    assertThat(result.getHardFailedCount(), is(2));
    assertThat(result.getSoftFailedCount(), is(1));
    assertThat(result.getFailedCount(), is(3));
  }

  @Test
  void parallelExecution_shouldBeEfficient() throws InterruptedException {
    User user = new User("John", "john@example.com", 25);

    Validator<User> slowValidator =
        target -> {
          try {
            Thread.sleep(100);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          return ValidationResult.valid();
        };

    ValidationChain<User> parallelChain =
        ValidationChain.<User>builder()
            .parallel()
            .addHardRule("slow-rule-1", slowValidator)
            .addHardRule("slow-rule-2", slowValidator)
            .addHardRule("slow-rule-3", slowValidator)
            .build();

    ValidationChainResult result = parallelChain.validate(user);

    assertThat(result.isPassed(), is(true));
    assertThat(result.getTotalExecutionTime().toMillis(), lessThan(250L));
  }

  @Test
  void sequentialExecution_shouldRunInOrder() throws InterruptedException {
    User user = new User("John", "john@example.com", 25);

    Validator<User> slowValidator =
        target -> {
          try {
            Thread.sleep(50);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
          }
          return ValidationResult.valid();
        };

    ValidationChain<User> sequentialChain =
        ValidationChain.<User>builder()
            .sequential()
            .addHardRule("slow-rule-1", slowValidator)
            .addHardRule("slow-rule-2", slowValidator)
            .build();

    ValidationChainResult result = sequentialChain.validate(user);

    assertThat(result.isPassed(), is(true));
    assertThat(result.getTotalExecutionTime().toMillis(), greaterThan(90L));
  }

  @Test
  void validationRuleBuilder_shouldWork() {
    User user = new User("Jo", "john@example.com", 25);

    ValidationRule<User> rule =
        ValidationRule.<User>builder("name-length")
            .validator(Validators.minLength("name", User::getName, 3))
            .hard()
            .build();

    ValidationChain<User> chain = ValidationChain.<User>builder().addRule(rule).build();

    ValidationChainResult result = chain.validate(user);

    assertThat(result.isPassed(), is(false));
    assertThat(result.getHardFailedRules().get(0).getRuleName(), is("name-length"));
  }

  @Test
  void getAllErrors_shouldCollectAllErrors() {
    User user = new User("", "invalid", 15);

    ValidationChain<User> chain =
        ValidationChain.<User>builder()
            .addHardRule("name-required", Validators.notEmpty("name", User::getName))
            .addHardRule("email-valid", Validators.email("email", User::getEmail))
            .addHardRule("age-valid", Validators.min("age", User::getAge, 18))
            .build();

    ValidationChainResult result = chain.validate(user);

    assertThat(result.getAllErrors(), hasSize(3));
  }

  @Test
  void summary_shouldProvideReadableOutput() {
    User user = new User("John", "john@example.com", 25);

    ValidationChain<User> chain =
        ValidationChain.<User>builder()
            .addHardRule("name-required", Validators.notEmpty("name", User::getName))
            .addHardRule("email-valid", Validators.email("email", User::getEmail))
            .build();

    ValidationChainResult result = chain.validate(user);

    assertThat(result.getSummary(), containsString("PASSED"));
    assertThat(result.getSummary(), containsString("2/2"));
  }

  @Test
  void detailedReport_shouldShowAllResults() {
    User user = new User("", "invalid", 25);

    ValidationChain<User> chain =
        ValidationChain.<User>builder()
            .addHardRule("name-required", Validators.notEmpty("name", User::getName))
            .addSoftRule("email-valid", Validators.email("email", User::getEmail))
            .build();

    ValidationChainResult result = chain.validate(user);
    String report = result.getDetailedReport();

    assertThat(report, containsString("HARD FAILED RULES"));
    assertThat(report, containsString("SOFT FAILED RULES"));
    assertThat(report, containsString("name-required"));
    assertThat(report, containsString("email-valid"));
  }

  @Test
  void exceptionInValidator_shouldBeCaught() {
    User user = new User("John", "john@example.com", 25);

    Validator<User> throwingValidator =
        target -> {
          throw new RuntimeException("Unexpected error");
        };

    ValidationChain<User> chain =
        ValidationChain.<User>builder().addHardRule("throwing-rule", throwingValidator).build();

    ValidationChainResult result = chain.validate(user);

    assertThat(result.isPassed(), is(false));
    assertThat(result.getHardFailedRules().get(0).hasException(), is(true));
    assertThat(
        result.getHardFailedRules().get(0).getErrorMessage(), containsString("Unexpected error"));
  }
}
