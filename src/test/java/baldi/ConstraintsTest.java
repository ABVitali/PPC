package baldi;

import baldi.constraint.Constraint;
import baldi.constraint.impl.Ac2001Constraint;
import baldi.constraint.impl.Ac3Constraint;
import baldi.constraint.impl.Ac4Constraint;
import baldi.constraint.impl.Ac6Constraint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class ConstraintsTest {
    private static final List<String> ALGS = Arrays.asList("AC3", "AC4", "AC6", "AC2001");

    @Before
    public void before() {
    }

    private Variable<Integer> var(String name, Propagation propagation, Integer... values) {
        List<Integer> domain = new LinkedList<>();
        domain.addAll(asList(values));
        return new Variable<>(name, domain, propagation);
    }

    private Variable<Integer> varRange(String name, Propagation propagation, Integer from, Integer to) {
        List<Integer> domain = IntStream.range(from, to).boxed().collect(Collectors.toList());
        return new Variable<>(name, domain, propagation);

    }

    private Constraint<Integer> constr(Variable<Integer> var1, BinaryOperator<Integer> operator,
                                       Variable<Integer> var2,
                                       String alg) {
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
    public void three() {
        for (String alg : ALGS) {
            Propagation propagation = new Propagation();
            Variable<Integer> x = var("x", propagation, 1, 2, 3, 4, 5, 6, 7, 8);
            Variable<Integer> y = var("y", propagation, 1, 2, 3, 4, 5, 6, 7, 8);
            Variable<Integer> z = var("z", propagation, 1, 2, 3, 4, 5, 6, 7, 8);
            propagation.add(constr(x, (var1, var2) -> var1 * 2 != var2, y, "AC3"));
            propagation.add(constr(x, (var1, var2) -> var1 % 2 == var2 % 2, y, "AC3"));
            propagation.add(constr(x, (var1, var2) -> var1 * 3 == var2, z, "AC3"));
        }
    }

    @Test
    public void prettySimple() {
        for (String alg : ALGS) {
            Propagation propagation = new Propagation();
            Variable<Integer> x = var("x", propagation, 1, 2);
            Variable<Integer> y = var("y", propagation, 1);
            Constraint<Integer> constr = constr(x, (int1, int2) -> int1 != int2, y, alg);
            propagation.add(constr);
            propagation.run();
            Assert.assertEquals(singletonList(1), y.getDomain());
            Assert.assertEquals(singletonList(2), x.getDomain());
        }
    }

    @Test
    public void oneSolution() {
        for (String alg : ALGS) {
            Propagation propagation = new Propagation();
            Variable<Integer> x = var("x", propagation, 1, 2, 3, 4, 5);
            Variable<Integer> y = var("y", propagation, 1);
            Variable<Integer> z = var("z", propagation, 2, 4);
            Constraint<Integer> constraint = constr(x, (int1, int2) -> int1 != int2, y, alg);
            propagation.add(constraint);
            propagation.run();
            Assert.assertEquals(asList(2, 3, 4, 5), x.getDomain());
            Assert.assertEquals(singletonList(1), y.getDomain());
            //{2,3,4,5} < {2,4} -> {2,3}, {4}
            Constraint<Integer> constraint2 = constr(x, (int1, int2) -> int1 < int2, z, alg);
            propagation.add(constraint2);
            propagation.run();
            Assert.assertEquals(asList(2, 3), x.getDomain());
            Assert.assertEquals(singletonList(4), z.getDomain());
            //{2,3} +1= {4} -> {3} , {4}
            Constraint<Integer> constraint3 = constr(x, (int1, int2) -> int1 + 1 == int2, z, alg);
            propagation.add(constraint3);
            propagation.run();
            Assert.assertEquals(singletonList(3), x.getDomain());
            Assert.assertEquals(singletonList(4), z.getDomain());
        }
    }

    @Test
    public void noSolution() {
        for (String alg : ALGS) {
            Propagation propagation = new Propagation();
            Variable<Integer> x = var("x", propagation, 1, 2, 3, 4, 5);
            Variable<Integer> y = var("y", propagation, 1, 2, 3, 4, 5);
            Variable<Integer> z = var("z", propagation, 2, 3, 4, 5, 6, 7, 9);
            Constraint<Integer> constraint = constr(x, (int1, int2) -> int1 * 2 == int2, y, alg);
            //   {1,2,3,4,5} *2 = {1,2,3,4,5}  -> {1,2}, {2,4}
            propagation.add(constraint);
            propagation.run();
            Assert.assertEquals(asList(1, 2), x.getDomain());
            Assert.assertEquals(asList(2, 4), y.getDomain());
            //{1,2} == {2,3,4,5,6,7,9} -> {2}, {2}
            Constraint<Integer> constraint2 = constr(x, (int1, int2) -> int1 == int2, z, alg);
            propagation.add(constraint2);
            propagation.run();
            Assert.assertEquals(singletonList(2), x.getDomain());
            Assert.assertEquals(singletonList(2), z.getDomain());
            //{2} +1= {2} -> {} , {}
            Constraint<Integer> constraint3 = constr(x, (int1, int2) -> int1 + 1 == int2, z, alg);
            propagation.add(constraint3);
            propagation.run();
            Assert.assertTrue(x.getDomain().isEmpty() || z.getDomain().isEmpty());
        }
    }

    @Test
    public void big() {
        for (String alg : ALGS) {
            Propagation propagation = new Propagation();
            Variable<Integer> x = varRange("x", propagation, 1, 10000);
            Variable<Integer> y = varRange("y", propagation, 500, 15000);
            Constraint<Integer> constraint = constr(x, (int1, int2) -> int1 * int1 == int2, y, alg);
            Constraint<Integer> constraint1 = constr(x, (int1, int2) -> int1 + int2 <= 900, y, alg);
            propagation.add(constraint);
            propagation.add(constraint1);
            propagation.run();

            Assert.assertEquals(asList(23, 24, 25, 26, 27, 28, 29), x.getDomain());
            Assert.assertEquals(asList(23 * 23, 24 * 24, 25 * 25, 26 * 26, 27 * 27, 28 * 28, 29 * 29), y.getDomain());
        }
    }
    @Test
    public void noSolutionButConsistent() {
        for (String alg : ALGS) {
            Propagation propagation = new Propagation();
            Variable<Integer> x = var("x", propagation, 1, 2);
            Variable<Integer> y = var("y", propagation, 1, 2);
            Variable<Integer> z = var("z", propagation, 1, 2);
            Constraint<Integer> constr = constr(x, (int1, int2) -> int1 != int2, y, alg);
            Constraint<Integer> constr1 = constr(x, (int1, int2) -> int1 != int2, z, alg);
            Constraint<Integer> constr2 = constr(z, (int1, int2) -> int1 != int2, y, alg);
            propagation.add(constr);
            propagation.add(constr1);
            propagation.add(constr2);
            propagation.run();
            Assert.assertEquals(asList(1, 2), x.getDomain());
            Assert.assertEquals(asList(1, 2), y.getDomain());
            Assert.assertEquals(asList(1, 2), z.getDomain());

        }
    }


}