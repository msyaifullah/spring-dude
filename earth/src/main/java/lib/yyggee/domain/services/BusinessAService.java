package lib.yyggee.domain.services;

import lib.yyggee.domain.model.Thing;
import lib.yyggee.domain.rules.IRule;

public class BusinessAService {

  public boolean evaluate(IRule rule, Thing thing) {
    return rule.isSatisfied(thing);
  }
}
