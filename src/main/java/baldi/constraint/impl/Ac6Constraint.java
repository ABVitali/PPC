package baldi.constraint.impl;

import baldi.constraint.Constraint;
import baldi.BinaryOperator;
import baldi.Variable;

import java.util.*;

public class Ac6Constraint<T> extends Constraint<T> {

    private final Map<Variable<T>, Queue<T>> queue;
    private final Map<Variable<T>, Map<T, List<T>>> s;

    public Ac6Constraint(Variable<T> first, BinaryOperator<T> operator, Variable<T> second) {
        super(first, operator, second);
        queue = new HashMap<>();
        queue.put(first, new LinkedList<>());
        queue.put(second, new LinkedList<>());
        s = new HashMap<>();
        s.put(first, new HashMap<>());
        s.put(second, new HashMap<>());
    }

    @Override
    public void init(Variable<T> xi) {
        Variable<T> xj = other(xi).get();
        List<T> toBeRemoved = new LinkedList<>();
        Map<T, List<T>> sXj = s.get(xj);
        Queue<T> qXi = queue.get(xi);
        for (T vi : xi.getDomain()) {
            boolean found = false;
            List<T> domain = xj.getDomain();
            for (int i = 0; i < domain.size() && !found; i++) {
                T vj = domain.get(i);
                if (apply(xj, vj, xi, vi)) {
                    found = true;
                    sXj.putIfAbsent(vj, new LinkedList<>());
                    sXj.get(vj).add(vi);
                }
            }
            if (!found) {
                toBeRemoved.add(vi);
                qXi.add(vi);
            }
        }
        toBeRemoved.forEach(xi::removeValue);
    }


    @Override
    public boolean filterFrom(Variable<T> xi, Variable<T> xj) {
        Queue<T> qXj = queue.get(xj);
        Map<T, List<T>> sXj = s.get(xj);
        Queue<T> qXi = queue.get(xi);
        if (xi.getDomain().isEmpty()) {
            return true;
        }
        while (!qXj.isEmpty()) {
            for (Iterator<T> iterator = qXj.iterator(); iterator.hasNext(); ) {
                T vj = iterator.next();
                for (T vi : sXj.getOrDefault(vj, Collections.emptyList())) {
                    if (xi.isInDomain(vi)) {
                        boolean found = false;
                        List<T> domain = xj.getDomain();
                        for (int i = domain.indexOf(vj) + 1; i < domain.size(); i++) {
                            T vjFst = domain.get(i);
                            if (apply(xj, vjFst, xi, vi)) {
                                found = true;
                                sXj.putIfAbsent(vjFst, new LinkedList<>());
                                sXj.get(vjFst).add(vi);
                            }
                        }
                        if (!found) {
                            xi.removeValue(vi);
                            qXi.add(vi);
                            if (xi.getDomain().isEmpty()) {
                                return true;
                            }
                        }
                    }
                }
                iterator.remove();
            }
        }
        return false;
    }
}
