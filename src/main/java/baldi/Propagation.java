package baldi;

import baldi.constraint.Constraint;

import java.util.*;

public class Propagation {
    private Queue<Variable<?>> queue;
    private Set<Constraint<?>> constraints;

    public Propagation() {
        this.queue = new LinkedList<>();
        this.constraints = new HashSet<>();
    }

    public void add(Variable<?> variable) {
        if (!queue.contains(variable)) {
            queue.add(variable);
        }

    }

    public void add(Constraint<?> constraint) {
        constraints.add(constraint);
        add(constraint.getFirst());
        add(constraint.getSecond());
    }

    @SuppressWarnings("unchecked")
    public boolean run() {
        while (!queue.isEmpty()) {
            Variable var = queue.poll();
            for (Constraint constraint : constraints) {
                constraint.init();
                //queue is populated on domain changes
                if (constraint.contains(var) && constraint.filterFrom(var) && var.getDomain().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

}
