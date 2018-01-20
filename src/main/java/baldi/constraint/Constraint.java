package baldi.constraint;

import baldi.BinaryOperator;
import baldi.Variable;

import java.util.Optional;

public abstract class Constraint<T> {
    private Variable<T> first;
    private final BinaryOperator<T> operator;
    private Variable<T> second;
    private boolean isInit;

    public Constraint(Variable<T> first, BinaryOperator<T> operator, Variable<T> second) {
        this.first = first;
        this.operator = operator;
        this.second = second;
        isInit = false;
    }

    protected boolean apply(T aValue, T anotherValue) {
        return operator.apply(aValue, anotherValue);
    }

    public boolean contains(Variable<T> var) {
        return getFirst().equals(var) || getSecond().equals(var);
    }

    protected boolean apply(Variable<T> x, T xValue, Variable<T> y, T yValue) {
        if (x.equals(first)) {
            return apply(xValue, yValue);
        } else if (x.equals(second)) {
            return apply(yValue, xValue);
        } else {
            return true;
        }
    }


    public Optional<Variable<T>> other(Variable<T> var) {
        if (getSecond().equals(var)) {
            return Optional.of(getFirst());
        } else if (getFirst().equals(var)) {
            return Optional.of(getSecond());
        } else {
            return Optional.empty();
        }
    }

    public boolean filterFrom(Variable<T> xi) {
        return other(xi).map(xj -> filterFrom(xi, xj)).orElse(false);
    }

    public void init() {
        if (!isInit) {
            init(getFirst());
            init(getSecond());
            //isInit = true;
        }
    }

    public abstract boolean filterFrom(Variable<T> xi, Variable<T> xj);

    public abstract void init(Variable<T> xi);

    public Variable<T> getFirst() {
        return first;
    }

    public Variable<T> getSecond() {
        return second;
    }


    public BinaryOperator<T> getOperator() {
        return operator;
    }


    public void replace(Variable<T> var) {
        if (getFirst().getName().equals(var.getName())) {
            first = var;
        } else if (getSecond().getName().equals(var.getName())) {
            second = var;
        }
    }

}

