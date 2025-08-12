package ex.nervisking.command;

// Funcional interface para tab completer que acepta tres argumentos
@FunctionalInterface
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}