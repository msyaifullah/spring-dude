package lib.yyggee.domain.rules;

import lib.yyggee.domain.model.Thing;

public interface IRule {
  boolean isSatisfied(Thing thing);
}
