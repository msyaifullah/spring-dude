package lib.yyggee.domain.rules;

public class Rule {
    public static IRule and(IRule... rules) {
        return new RuleAnd(rules);
    }

    public static IRule or(IRule... rules) {
        return new RuleOr(rules);
    }
}
