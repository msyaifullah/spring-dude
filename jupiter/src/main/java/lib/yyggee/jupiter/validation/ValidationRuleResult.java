package lib.yyggee.jupiter.validation;

import java.time.Duration;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidationRuleResult {

  private final String ruleName;
  private final boolean passed;
  private final FailMode failMode;
  private final List<ValidationError> errors;
  private final Duration executionTime;
  private final Throwable exception;

  public boolean isFailed() {
    return !passed;
  }

  public boolean isHardFail() {
    return !passed && failMode == FailMode.HARD;
  }

  public boolean isSoftFail() {
    return !passed && failMode == FailMode.SOFT;
  }

  public boolean hasException() {
    return exception != null;
  }

  public String getErrorMessage() {
    if (passed) {
      return null;
    }
    if (exception != null) {
      return exception.getMessage();
    }
    if (errors != null && !errors.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      for (ValidationError error : errors) {
        if (!sb.isEmpty()) {
          sb.append("; ");
        }
        sb.append(error.getField()).append(": ").append(error.getMessage());
      }
      return sb.toString();
    }
    return "Validation failed";
  }
}
