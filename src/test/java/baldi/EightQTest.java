package baldi;


import baldi.constraint.Constraint;
import baldi.constraint.impl.Ac2001Constraint;
import baldi.constraint.impl.Ac3Constraint;
import baldi.constraint.impl.Ac4Constraint;
import baldi.constraint.impl.Ac6Constraint;
import baldi.solver.ForwardSolver;
import baldi.solver.Solver;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class EightQTest {

    private class Square implements Comparable<Square> {
        private final int dimension;
        private final int row;
        private final int col;

        private Square(int dimension, int row, int col) {
            this.dimension = dimension;
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public boolean sameRowAs(Square q) {
            return this.row == q.row;
        }

        public boolean sameColAs(Square q) {
            return this.col == q.col;
        }

        public boolean sameDiagonalAs(Square q) {
            return Math.abs(row - q.row) == Math.abs(col - q.col);

        }

        @Override
        public int compareTo(Square o) {
            return (this.col - o.col) * dimension + (this.row - o.row);
        }

        @Override
        public String toString() {
            return "" + ((char) (col + 65)) + (row + 1);

        }
    }

    private Constraint<Square> constr(Variable<Square> var1, BinaryOperator<Square> operator,
                                      Variable<Square> var2, String alg) {
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

    @Test
    public void eight() {
        int k = 8;
        for (String alg : Arrays.asList("AC3", "AC4")) {
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
                    constraints.add(constr(square1, (queen2, queen1) -> !queen1.sameRowAs(queen2), square2, alg));
                    constraints.add(constr(square1, (queen2, queen1) -> !queen1.sameDiagonalAs(queen2), square2, alg));
                }
            }
            Solver<Square> solver = new ForwardSolver<>(variables, constraints, propagation);
            System.out.print(alg + ": ");
            List<Square> squares = solver.firstSolution();
            squares.forEach((value) -> System.out.print(value + " "));
            System.out.println();
            final int[] q = {0};
            squares.forEach(s -> q[0]++);
            Assert.assertEquals(q[0], k);

        }


    }
}
