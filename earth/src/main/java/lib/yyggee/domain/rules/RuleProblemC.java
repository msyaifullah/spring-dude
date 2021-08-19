package lib.yyggee.domain.rules;

import lib.yyggee.domain.model.Thing;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RuleProblemC implements IRule {

    private final String message;

    public RuleProblemC(String message){
        this.message = message;
    }

    @Override
    public boolean isSatisfied(Thing thing) {
        log.info("is problem C executed {} {}", message, false);
        return false;
    }


}
