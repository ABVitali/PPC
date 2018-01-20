package baldi.solver;

import baldi.Propagation;
import baldi.Variable;
import baldi.constraint.Constraint;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class ForwardSolver<T> implements Solver<T> {
    private final List<Constraint<T>> constraints;
    private Propagation propagation;
    private final List<Variable<T>> variables;
    private final Map<Integer, Variable<T>> selectedVariables;
    private HashMap<Variable<T>, T> solution;

    public ForwardSolver(List<Variable<T>> variables, List<Constraint<T>> constraints, Propagation propagation) {
        this.variables = variables;
        this.constraints = constraints;
        this.propagation = propagation;
        this.selectedVariables = new HashMap<>();
    }

    @Override
    public List<T> firstSolution() {
        solution = new HashMap<>();
        int i = 0;
        while (0 <= i && i < variables.size()) {
            if (variables.get(i).getDomain().isEmpty()) {
                //We have found that the current solution lead to an empty domain
            	i--;//We must go back
                revert(i);//Undo the setting of the i-1 variable
                resetFrom(i);//Reset all the domains of the (i,n) variables; the reset reapplies the constraints
            } else if (next(select(i))) {//select choose the variable and select the value, next check the consistency
                i++;
            } else {
                revert(i);//consistency was not found, remove the selected value, retry with another value next while
            }
        }
        return solution.values().stream().sorted().collect(toList());
    }

    // Reset all the domain after the ith variable, and reapplies the constraints of variabiles <i
    private void resetFrom(int i) {
        for (int j = i + 1; j < variables.size(); j++) {
            Variable<T> var = variables.get(j);
            var.resetDomain();
            int i1 = variables.indexOf(var);
            constraints.stream()
                    .filter(constr -> constr.other(var).map(oldVar -> variables.indexOf(oldVar) < i1).orElse(false))
                    .forEach(constr -> constr.other(var).ifPresent(other -> {
                        constr.init(other);
                        constr.filterFrom(other);
                    }));
        }
    }

    //Select a value by creating a copy of the variable (that are identified just by their name) and putting the
    // original variable into a map, the original variabile will be reused if it will be reverted.
    private Variable<T> select(int i) {
        Variable<T> var = variables.get(i);
        List<T> domain = var.getDomain();
        T value = domain.get(0);
        solution.put(var, value);
        var.removeValue(value);
        selectedVariables.put(i, var);//variables are identified by their names, so are still valid
        Variable<T> newVar = new Variable<>(var.getName(), Collections.singletonList(value), propagation);
        variables.set(i, newVar);
        constraints.forEach(constr -> constr.replace(newVar));
        return newVar;
    }

    //Full look Ahead.
    private boolean next(final Variable<T> cVar) {
        int cv = variables.indexOf(cVar);
        List<Constraint<T>> cConstrs = constraints.stream()
                .filter(constr -> constr.getFirst().equals(cVar) && variables.indexOf(constr.getSecond()) > cv)
                .collect(toList());
        boolean consistent = true;
        Variable<T> vm;
        while (!cConstrs.isEmpty() && consistent) {
            Constraint<T> constraint = cConstrs.remove(0);
            Variable<T> vk = constraint.getSecond();
            vm = constraint.getFirst();
            List<T> kDomain = vk.getDomain();
            constraint.init(vm);
            if (constraint.filterFrom(vm)) {
                constraints.stream()
                        .filter(constr -> constr.getFirst().equals(vk) && variables.indexOf(constr.getSecond()) > cv)
                        .filter(constr -> !cConstrs.contains(constr))
                        .forEach(cConstrs::add);
                consistent = !kDomain.isEmpty();
            }
        }
        return consistent;

    }

    //Restore the saved variable, discarding the previously selected value
    private void revert(int i) {
        Variable<T> var = selectedVariables.remove(i);
        variables.set(i, var);//selected value was already removed
        constraints.forEach(constr -> constr.replace(var));
    }
}
