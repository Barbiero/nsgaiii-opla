package br.ufpr.inf.opla.patterns.operator.impl;

import br.ufpr.inf.opla.patterns.operator.AbstractMutationOperator;
import br.ufpr.inf.opla.patterns.strategies.designpatternselection.DesignPatternSelectionStrategy;
import br.ufpr.inf.opla.patterns.strategies.scopeselection.ScopeSelectionStrategy;
import jmetal.core.Solution;
import jmetal.util.PseudoRandom;

import java.util.HashMap;

public class DesignPatternsAndPLAMutationOperator extends AbstractMutationOperator {

    private final PLAMutation pLAMutation;
    private final DesignPatternMutationOperator designPatternMutationOperator;

    public DesignPatternsAndPLAMutationOperator(HashMap<String, Object> parameters, ScopeSelectionStrategy scopeSelectionStrategy, DesignPatternSelectionStrategy designPatternSelectionStrategy) {
        super(parameters);
        pLAMutation = new PLAMutation(parameters);
        designPatternMutationOperator = new DesignPatternMutationOperator(parameters, scopeSelectionStrategy, designPatternSelectionStrategy);
    }

    @Override
    protected boolean hookMutation(Solution solution, Double probability) throws Exception {
        int random = PseudoRandom.randInt(0, 6);
        if (random == 0) {
            return designPatternMutationOperator.hookMutation(solution, probability);
        } else {
            return pLAMutation.hookMutation(solution, probability);
        }
    }

}
