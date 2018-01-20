# PPC
AC-Solver: Programmation par Contraintes

## How to import the project


## How to create Variables
It is possible to create some private methods to define more comfortable ways to add variables. For example in the actual main file there are "var" and "varRange". The only important thing is to define a type of the variable and to respect the sign of the constructor method. For example in the case of 8-queens test the variables are of type "Square" that represent a cell of the board.

```
Variable constructor method sign:
   
   public Variable(String name, List<T> domain, Propagation propagation) {

private method var:
   
   private static Variable<Integer> var(String name, Propagation propagation, Integer... values) {
        List<Integer> domain = new LinkedList<>();
        domain.addAll(asList(values));
        return new Variable<>(name, domain, propagation);
   }

private method varRange:
   
   private static Variable<Integer> varRange(String name, Propagation propagation, Integer from, Integer to) {
        List<Integer> domain = IntStream.range(from, to).boxed().collect(Collectors.toList());
        return new Variable<>(name, domain, propagation);
   }
   
```
#### N.B. to use after the class that implements the Backtracking it is needed to encapsulate all the variables in a list of variables.

```
List<Variable<Integer>> variables = new LinkedList<>();
Variable<Integer> x = var("x", propagation, 1, 2);
Variable<Integer> y = var("y", propagation, 1);
```

## How to create Constraints
It is possible to create some private methods to define more comfortable ways to write constraints. For example in the actual main file there are "constrInteger" and "constrSquare". The key part of this implementation is the usage of the class BinaryOperator that allows the user to write his own operator for any class needed. This means that we can define our own constraints of every type that can be useful for the actual target.
The parameter "alg" is only a way to filter from using one or more AC-Algorithms.

Some examples:
```
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
  
Constraint<Integer> constraint = constrInteger(x, (int1, int2) -> int1 * int1 == int2, y, alg); //in this way we define that the square of a numeber (x) must be equal to the value of the second number

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

Constraint<Square> constraintSquareRow = constrSquare(square1, (queen2, queen1) -> !queen1.sameRowAs(queen2), square2, alg);
Constraint<Square> constraintSquareDiagonal = (constrSquare(square1, (queen2, queen1) -> !queen1.sameDiagonalAs(queen2), square2, alg));
   
```
#### N.B. to use after the class that implements the Backtracking it is needed to encapsulate all the constraints in a list of constraints.

```
List<Constraint<Integer>> constraints = new LinkedList<>();
constraints.add(constr); //where constr has been already defined
```

## How to add all to Propagation
First it is needed to create an object of type Propagation:

```
Propagation propagation = new Propagation();
```

Then we must add constraints to the propagation

```
propagation.add(constr); //constr must be already defined in the program
```

And in the end we can run the Propagation

```
propagation.run();
```

At this point we will have filtered all the domains of the variables using the constraints choosen before. Now we want to look for a single solution using the Backtracking solver.


## How to run the Backtracking
First we need to instanciate an object of type "Solver<VariableType>" where variable type must be substituted with the type of the variables.

```
Solver<Integer> solver = new ForwardSolver<>(variables, constraints, propagation);
```

Then we need to run the solver and to print the solution to the user

```
Solver<Integer> solver = new ForwardSolver<>(variables, constraints, propagation);
System.out.print(alg + ": ");
List<Integer> solution = solver.firstSolution();
solution.forEach((value) -> System.out.print(value + " "));
System.out.println();
```
## Known problems.

The Backtracking algorithm is not working with AC6 and AC2001 algorithm. I couldn't find exactly the problem.
