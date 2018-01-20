package baldi;

import baldi.constraint.Constraint;
import baldi.constraint.impl.Ac2001Constraint;
import baldi.constraint.impl.Ac3Constraint;
import baldi.constraint.impl.Ac4Constraint;
import baldi.constraint.impl.Ac6Constraint;
import baldi.solver.ForwardSolver;
import baldi.solver.Solver;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {

	public static void main(String[] args) {
		//test1 on Integer to verify AC-Algorithms filtering
		for (String alg : Arrays.asList("AC3", "AC4", "AC6", "AC2001")) {
			List<Variable<Integer>> variables = new LinkedList<>();
            List<Constraint<Integer>> constraints = new LinkedList<>();
            Propagation propagation = new Propagation();
            Variable<Integer> x = var("x", propagation, 1, 2);
            Variable<Integer> y = var("y", propagation, 1);
            Constraint<Integer> constr = constrInteger(x, (int1, int2) -> int1 != int2, y, alg);
            variables.add(x);
            variables.add(y);
            constraints.add(constr);
            propagation.add(constr);
            propagation.run();
            Solver<Integer> solver = new ForwardSolver<>(variables, constraints, propagation);
            System.out.print(alg + ": ");
            List<Integer> solution = solver.firstSolution();
            solution.forEach((value) -> System.out.print(value + " "));
            System.out.println();
		}
		
		//test2 on Integer to verify AC-Algorithms filtering
		for (String alg : Arrays.asList("AC3", "AC4", "AC6", "AC2001")) {
			List<Variable<Integer>> variables = new LinkedList<>();
            List<Constraint<Integer>> constraints = new LinkedList<>();
			Propagation propagation = new Propagation();
	        Variable<Integer> x = varRange("x", propagation, 1, 10000);
	        Variable<Integer> y = varRange("y", propagation, 500, 15000);
	        Constraint<Integer> constraint = constrInteger(x, (int1, int2) -> int1 * int1 == int2, y, alg);
	        Constraint<Integer> constraint1 = constrInteger(x, (int1, int2) -> int1 + int2 <= 900, y, alg);
	        propagation.add(constraint);
	        propagation.add(constraint1);
	        variables.add(x);
	        variables.add(y);
	        constraints.add(constraint);
	        constraints.add(constraint1);
	        propagation.run();
	        Solver<Integer> solver = new ForwardSolver<>(variables, constraints, propagation);
	        System.out.print(alg + ": ");
            List<Integer> solution = solver.firstSolution();
            solution.forEach((value) -> System.out.print(value + " "));
            System.out.println();
		}
		
		//8 queens
			int k = 8;
	        for (String alg : Arrays.asList("AC3", "AC4", "AC6", "AC2001")) {
	            List<Variable<Square>> variables = new LinkedList<>();
	            List<Constraint<Square>> constraints = new LinkedList<>();
	            Propagation propagation = new Propagation();
	            for (int queen = 0; queen < k; queen++) {
	                List<Square> board = new LinkedList<>();
	                for (int col = 0; col < k; col++) {
	                    board.add(new Square(k, col, queen));
	                }
	                variables.add(new Variable<>("Q" + (queen + 1), board, propagation));
	            }
	            for (int i = 0; i < variables.size(); i++) {
	                for (int j = i + 1; j < variables.size(); j++) {
	                    Variable<Square> square1 = variables.get(i);
	                    Variable<Square> square2 = variables.get(j);
	                    Constraint<Square> constraintSquareRow = constrSquare(square1, (queen2, queen1) -> !queen1.sameRowAs(queen2), square2, alg);
	                    Constraint<Square> constraintSquareDiagonal = (constrSquare(square1, (queen2, queen1) -> !queen1.sameDiagonalAs(queen2), square2, alg)); 
	                    constraints.add(constraintSquareRow);
	                    constraints.add(constraintSquareDiagonal);
	                }
	            }
	            Solver<Square> solver = new ForwardSolver<>(variables, constraints, propagation);
	            System.out.print(alg + ": ");
	            List<Square> squares = solver.firstSolution();
	            squares.forEach((value) -> System.out.print(value + " "));
	            System.out.println();
	        }
	}
	
	private static Constraint<Square> constrSquare(Variable<Square> var1, BinaryOperator<Square> operator, Variable<Square> var2, String alg) {
		switch (alg) {
			case "AC3":
				return new Ac3Constraint<>(var1, operator, var2);
			case "AC4":
				return new Ac4Constraint<>(var1, operator, var2);
			case "AC6":
				return new Ac6Constraint<>(var1, operator, var2);
			case "AC2001":
				return new Ac2001Constraint<>(var1, operator, var2);
			default:
				throw new IllegalArgumentException("Not yet implemented");
		}
	}
	
	private static Constraint<Integer> constrInteger(Variable<Integer> var1, BinaryOperator<Integer> operator, Variable<Integer> var2, String alg) {
		switch (alg) {
			case "AC3":
				return new Ac3Constraint<>(var1, operator, var2);
			case "AC4":
				return new Ac4Constraint<>(var1, operator, var2);
			case "AC6":
				return new Ac6Constraint<>(var1, operator, var2);
			case "AC2001":
				return new Ac2001Constraint<>(var1, operator, var2);
			default:
				throw new IllegalArgumentException("Not yet implemented");
		}
	}
	
	private static Variable<Integer> var(String name, Propagation propagation, Integer... values) {
        List<Integer> domain = new LinkedList<>();
        domain.addAll(asList(values));
        return new Variable<>(name, domain, propagation);
    }
	
	private static Variable<Integer> varRange(String name, Propagation propagation, Integer from, Integer to) {
        List<Integer> domain = IntStream.range(from, to).boxed().collect(Collectors.toList());
        return new Variable<>(name, domain, propagation);

    }
}
