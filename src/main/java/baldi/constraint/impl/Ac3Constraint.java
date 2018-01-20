package baldi.constraint.impl;

import baldi.BinaryOperator;
import baldi.Variable;
import baldi.constraint.Constraint;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class Ac3Constraint<T> extends Constraint<T> {

    public Ac3Constraint(Variable<T> first, BinaryOperator<T> operator, Variable<T> second) {
        super(first, operator, second);
    }


    @Override
    public boolean filterFrom(Variable<T> xi, Variable<T> xj) {
        List<T> toBeRemoved;//this is to avoid concurrent modifications
        toBeRemoved = xj.getDomain().stream().filter(vj -> revise(xj, xi, vj)).collect(toList());
        return toBeRemoved.stream().map(xj::removeValue).reduce((bool1, bool2) -> bool1 || bool2).orElse(false);
    }

    @Override
    public void init(Variable<T> xi) {

    }

    private boolean revise(Variable<T> xj, Variable<T> xi, T vj) {
        return xi.getDomain().stream().noneMatch(vi -> apply(xj, vj, xi, vi));
    }

}
