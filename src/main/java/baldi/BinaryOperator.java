package baldi;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T, T, Boolean> {
}
