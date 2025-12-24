package lib.yyggee.domain.rules;

import java.util.Arrays;
import lib.yyggee.domain.model.Thing;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleOr implements IRule {
  private final IRule[] rules;

  public RuleOr(IRule... rules) {
    this.rules = rules;
  }

  @Override
  public boolean isSatisfied(Thing thing) {
    log.info("is Rule OR executed");
    return Arrays.stream(this.rules)
        .anyMatch(
            iRule -> {
              return iRule.isSatisfied(thing);
            });
  }
}
