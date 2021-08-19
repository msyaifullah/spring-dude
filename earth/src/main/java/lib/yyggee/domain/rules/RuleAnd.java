package lib.yyggee.domain.rules;

import lib.yyggee.domain.model.Thing;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class RuleAnd implements IRule {
    private final IRule[] rules;

    public RuleAnd(IRule... rules) {
        this.rules = rules;
    }

    @Override
    public boolean isSatisfied(Thing thing) {
        log.info("is Rule And executed");
        return Arrays.stream(this.rules).allMatch(iRule -> {
            return iRule.isSatisfied(thing);
        });
    }

}
