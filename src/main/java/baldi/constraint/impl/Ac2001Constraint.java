package baldi.constraint.impl;

import baldi.constraint.Constraint;
import baldi.BinaryOperator;
import baldi.Variable;

import java.util.*;

public class Ac2001Constraint<T extends Comparable<T>> extends Constraint<T> {
    private Map<Variable<T>, Map<T, T>> last;

    public Ac2001Constraint(Variable<T> first, BinaryOperator<T> operator, Variable<T> second) {
        super(first, operator, second);
        last = new HashMap<>();
    }


    @Override
    public boolean filterFrom(Variable<T> xi, Variable<T> xj) {
        Map<T, T> lastXi = last.get(xi);
        List<T> toBeRemoved = new LinkedList<>();
        for (T vi : xi.getDomain()) {
            T lastVi = lastXi.get(vi);
            List<T> xjDomain = xj.getDomain();
            if (!xjDomain.contains(lastVi)) {//null doesn't belong to xjDomain
                Optional<T> oVj = xjDomain.stream()
                        .sorted()
                        .filter(vj -> lastVi == null || vj.compareTo(lastVi) > 0)
                        .filter(vj -> apply(xi, vi, xj, vj))
                        .findFirst();
                if (oVj.isPresent()) {
                    lastXi.put(vi, oVj.get());
                } else {
                    toBeRemoved.add(vi);
                }
            }
        }
        toBeRemoved.forEach(xi::removeValue);
        return !toBeRemoved.isEmpty();
    }

    @Override
    public void init(Variable<T> xi) {
        last.put(xi, new HashMap<>());
    }
}
