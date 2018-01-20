package baldi.constraint.impl;

import baldi.constraint.Constraint;
import baldi.BinaryOperator;
import baldi.Variable;

import java.util.*;

import static java.util.Collections.emptyList;

public class Ac4Constraint<T> extends Constraint<T> {

    private final Map<Variable<T>, Map<T, Integer>> counter;
    private final Map<Variable<T>, Map<T, List<T>>> s;
    private final Map<Variable<T>, Queue<T>> queue;

    public Ac4Constraint(Variable<T> first, BinaryOperator<T> operator, Variable<T> second) {
        super(first, operator, second);
        s = new HashMap<>();
        queue = new HashMap<>();
        counter = new HashMap<>();
        s.put(first, new HashMap<>());
        s.put(second, new HashMap<>());
        queue.put(first, new LinkedList<>());
        queue.put(second, new LinkedList<>());
        //init(second); init(first);
    }

    @Override
    public void init(Variable<T> xj) {
        Variable<T> xi = other(xj).get();
        counter.put(xi, new HashMap<>());
        queue.put(xi, new LinkedList<>());
        s.put(xj, new HashMap<>());
        List<T> toBeRemoved = new LinkedList<>();
        Map<T, Integer> counterXi = counter.get(xi);
        Map<T, List<T>> sXj = s.get(xj);
        Queue<T> qXi = queue.get(xi);
        for (T vi : xi.getDomain()) {
            int found = 0;
            for (T vj : xj.getDomain()) {
                if (apply(xi, vi, xj, vj)) {
                    found++;
                    sXj.putIfAbsent(vj, new LinkedList<>());
                    sXj.get(vj).add(vi);//this is foreach (vi,vj)€C
                }
            }
            counterXi.put(vi, found);
            if (found == 0) {
                toBeRemoved.add(vi);
                qXi.add(vi);
            }
        }
        for (T vi : toBeRemoved) {
            xi.removeValue(vi);
        }
    }

    @Override
    public boolean filterFrom(Variable<T> xi, Variable<T> xj) {
        boolean found = false;

        if (xi.getDomain().isEmpty()) {
            return true;
        }
        Map<T, Integer> counterXi = counter.get(xi);
        Map<T, List<T>> sXj = s.get(xj);
        Queue<T> qXi = queue.get(xi);
        Queue<T> qXj = queue.get(xj);
        while (!qXj.isEmpty()) {
            for (Iterator<T> iterator = qXj.iterator(); iterator.hasNext(); ) {
                T vj = iterator.next();
                iterator.remove();
                for (T vi : sXj.getOrDefault(vj, emptyList())) {
                    if (xi.isInDomain(vi)) {
                        if (counterXi.compute(vi, (key, value) -> value--).equals(0)) {
                            xi.removeValue(vi);
                            found = true;
                            qXi.add(vi);
                            if (xi.getDomain().isEmpty()) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return found;
    }

}
