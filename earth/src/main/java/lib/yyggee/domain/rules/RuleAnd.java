package lib.yyggee.domain.rules;

import java.util.Arrays;
import lib.yyggee.domain.model.Thing;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleAnd implements IRule {
  private final IRule[] rules;

  public RuleAnd(IRule... rules) {
    this.rules = rules;
  }

  @Override
  public boolean isSatisfied(Thing thing) {
    log.info("is Rule And executed");
    return Arrays.stream(this.rules)
        .allMatch(
            iRule -> {
              return iRule.isSatisfied(thing);
            });
  }
}
