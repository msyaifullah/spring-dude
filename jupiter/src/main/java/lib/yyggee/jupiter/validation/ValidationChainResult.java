package lib.yyggee.jupiter.validation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidationChainResult {

  private final boolean passed;
  private final boolean hasHardFailures;
  private final boolean hasSoftFailures;
  private final List<ValidationRuleResult> ruleResults;
  private final Duration totalExecutionTime;

  public boolean isFailed() {
    return !passed;
  }

  public List<ValidationRuleResult> getPassedRules() {
    return ruleResults.stream().filter(ValidationRuleResult::isPassed).collect(Collectors.toList());
  }

  public List<ValidationRuleResult> getFailedRules() {
    return ruleResults.stream().filter(ValidationRuleResult::isFailed).collect(Collectors.toList());
  }

  public List<ValidationRuleResult> getHardFailedRules() {
    return ruleResults.stream()
        .filter(ValidationRuleResult::isHardFail)
        .collect(Collectors.toList());
  }

  public List<ValidationRuleResult> getSoftFailedRules() {
    return ruleResults.stream()
        .filter(ValidationRuleResult::isSoftFail)
        .collect(Collectors.toList());
  }

  public List<ValidationError> getAllErrors() {
    List<ValidationError> allErrors = new ArrayList<>();
    for (ValidationRuleResult result : ruleResults) {
      if (result.getErrors() != null) {
        allErrors.addAll(result.getErrors());
      }
    }
    return Collections.unmodifiableList(allErrors);
  }

  public int getTotalRules() {
    return ruleResults.size();
  }

  public int getPassedCount() {
    return (int) ruleResults.stream().filter(ValidationRuleResult::isPassed).count();
  }

  public int getFailedCount() {
    return (int) ruleResults.stream().filter(ValidationRuleResult::isFailed).count();
  }

  public int getHardFailedCount() {
    return (int) ruleResults.stream().filter(ValidationRuleResult::isHardFail).count();
  }

  public int getSoftFailedCount() {
    return (int) ruleResults.stream().filter(ValidationRuleResult::isSoftFail).count();
  }

  public String getSummary() {
    return String.format(
        "Validation %s: %d/%d rules passed, %d hard failures, %d soft failures, took %dms",
        passed ? "PASSED" : "FAILED",
        getPassedCount(),
        getTotalRules(),
        getHardFailedCount(),
        getSoftFailedCount(),
        totalExecutionTime.toMillis());
  }

  public String getDetailedReport() {
    StringBuilder sb = new StringBuilder();
    sb.append("=== Validation Chain Report ===\n");
    sb.append(getSummary()).append("\n\n");

    if (!getPassedRules().isEmpty()) {
      sb.append("PASSED RULES:\n");
      for (ValidationRuleResult result : getPassedRules()) {
        sb.append(
            String.format(
                "  [OK] %s (took %dms)\n",
                result.getRuleName(), result.getExecutionTime().toMillis()));
      }
      sb.append("\n");
    }

    if (!getHardFailedRules().isEmpty()) {
      sb.append("HARD FAILED RULES:\n");
      for (ValidationRuleResult result : getHardFailedRules()) {
        sb.append(
            String.format(
                "  [HARD FAIL] %s (took %dms): %s\n",
                result.getRuleName(),
                result.getExecutionTime().toMillis(),
                result.getErrorMessage()));
      }
      sb.append("\n");
    }

    if (!getSoftFailedRules().isEmpty()) {
      sb.append("SOFT FAILED RULES:\n");
      for (ValidationRuleResult result : getSoftFailedRules()) {
        sb.append(
            String.format(
                "  [SOFT FAIL] %s (took %dms): %s\n",
                result.getRuleName(),
                result.getExecutionTime().toMillis(),
                result.getErrorMessage()));
      }
    }

    return sb.toString();
  }
}
