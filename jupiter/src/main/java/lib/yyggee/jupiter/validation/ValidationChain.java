package lib.yyggee.jupiter.validation;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ValidationChain<T> {

  private final List<ValidationRule<T>> rules;
  private final ExecutorService executor;
  private final boolean parallelExecution;

  private ValidationChain(
      List<ValidationRule<T>> rules, ExecutorService executor, boolean parallelExecution) {
    this.rules = new ArrayList<>(rules);
    this.executor = executor;
    this.parallelExecution = parallelExecution;
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public ValidationChainResult validate(T target) {
    Instant start = Instant.now();
    List<ValidationRuleResult> results;

    if (parallelExecution && rules.size() > 1) {
      results = executeParallel(target);
    } else {
      results = executeSequential(target);
    }

    Duration totalTime = Duration.between(start, Instant.now());

    boolean hasHardFailures = results.stream().anyMatch(ValidationRuleResult::isHardFail);
    boolean hasSoftFailures = results.stream().anyMatch(ValidationRuleResult::isSoftFail);
    boolean passed = !hasHardFailures;

    return ValidationChainResult.builder()
        .passed(passed)
        .hasHardFailures(hasHardFailures)
        .hasSoftFailures(hasSoftFailures)
        .ruleResults(results)
        .totalExecutionTime(totalTime)
        .build();
  }

  private List<ValidationRuleResult> executeSequential(T target) {
    List<ValidationRuleResult> results = new ArrayList<>();
    for (ValidationRule<T> rule : rules) {
      results.add(rule.execute(target));
    }
    return results;
  }

  private List<ValidationRuleResult> executeParallel(T target) {
    List<CompletableFuture<ValidationRuleResult>> futures =
        rules.stream()
            .map(rule -> CompletableFuture.supplyAsync(() -> rule.execute(target), executor))
            .collect(Collectors.toList());

    return futures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList());
  }

  public static class Builder<T> {
    private final List<ValidationRule<T>> rules = new ArrayList<>();
    private ExecutorService executor;
    private boolean parallelExecution = true;

    public Builder<T> addRule(ValidationRule<T> rule) {
      rules.add(rule);
      return this;
    }

    public Builder<T> addHardRule(String name, Validator<T> validator) {
      rules.add(ValidationRule.hard(name, validator));
      return this;
    }

    public Builder<T> addSoftRule(String name, Validator<T> validator) {
      rules.add(ValidationRule.soft(name, validator));
      return this;
    }

    public Builder<T> addRules(List<ValidationRule<T>> rules) {
      this.rules.addAll(rules);
      return this;
    }

    public Builder<T> parallel() {
      this.parallelExecution = true;
      return this;
    }

    public Builder<T> sequential() {
      this.parallelExecution = false;
      return this;
    }

    public Builder<T> executor(ExecutorService executor) {
      this.executor = executor;
      return this;
    }

    public Builder<T> parallelism(int parallelism) {
      this.executor = Executors.newFixedThreadPool(parallelism);
      return this;
    }

    public ValidationChain<T> build() {
      if (executor == null) {
        executor = Executors.newWorkStealingPool();
      }
      return new ValidationChain<>(rules, executor, parallelExecution);
    }
  }
}
