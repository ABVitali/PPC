package baldi;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Variable<T> implements Comparable {
    private final String name;
    private final List<T> domain;
    private final List<T> initialDomain;
    private final Propagation propagation;

    /**
     * Initialize a variable.
     *
     * @param name        The name of the variable.
     * @param domain      The domain of the variable. Is intended sorted.
     * @param propagation The propagation object.
     */
    public Variable(String name, List<T> domain, Propagation propagation) {
        this.name = name;
        this.domain = domain;
        this.initialDomain = new LinkedList<>();
        initialDomain.addAll(domain);
        this.propagation = propagation;
    }

    public boolean isInDomain(T a) {
        return getDomain().contains(a);
    }


    public void resetDomain(){
        domain.clear();
        domain.addAll(initialDomain);
    }

    public boolean removeValue(T a) {
        if (isInDomain(a)) {
            getDomain().remove(a);
            propagation.add(this);
            return true;
        }
        return false;
    }


    public List<T> getDomain() {
        return domain;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null) {
            return false;
        } else if ((o instanceof Variable)) {
            Variable var = (Variable) o;
            return var.getName().equals(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public int compareTo(Object o) {
        return getName().compareTo(((Variable) o).getName());
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
