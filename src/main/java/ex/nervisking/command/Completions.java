package ex.nervisking.command;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Clase que gestiona una lista de autocompletados (completions) para comandos.
 *
 * @since 1.1.0
 */
public class Completions {

    private final Set<String> completions;

    // Constructores
    public Completions() {
        this.completions = new LinkedHashSet<>();
    }

    public Completions(String... completions) {
        this();
        add(completions);
    }

    // Métodos estáticos de fábrica
    @Contract(value = " -> new", pure = true)
    public static @NotNull Completions of() {
        return new Completions();
    }

    @Contract("_ -> new")
    public static @NotNull Completions of(String... completions) {
        return new Completions(completions);
    }

    // Getters
    public List<String> getCompletions() {
        return List.copyOf(completions);
    }

    public List<String> asList() {
        return List.copyOf(completions);
    }

    public boolean hasCompletions(String value) {
        return value != null && completions.contains(value.toLowerCase());
    }

    // Métodos para agregar elementos
    public void add(String... values) {
        if (values == null) return;
        for (String val : values) {
            if (val != null && !val.isBlank()) {
                completions.add(val.toLowerCase());
            }
        }
    }

    public void add(List<String> values) {
        if (values == null) return;
        for (String val : values) {
            if (val != null && !val.isBlank()) {
                completions.add(val.toLowerCase());
            }
        }
    }

    public void add(Set<String> values) {
        if (values == null) return;
        for (String val : values) {
            if (val != null && !val.isBlank()) {
                completions.add(val.toLowerCase());
            }
        }
    }

    public void add(Integer... values) {
        if (values == null) return;
        for (Integer val : values) {
            if (val != null) {
                completions.add(val.toString());
            }
        }
    }

    public void addIntegers(List<Integer> values) {
        if (values == null) return;
        for (Integer val : values) {
            if (val != null) {
                completions.add(val.toString());
            }
        }
    }

    public void add(Double... values) {
        if (values == null) return;
        for (Double val : values) {
            if (val != null) {
                completions.add(val.toString());
            }
        }
    }

    public void addDoubles(List<Double> values) {
        if (values == null) return;
        for (Double val : values) {
            if (val != null) {
                completions.add(val.toString());
            }
        }
    }

    public void add(Boolean... values) {
        if (values == null) return;
        for (Boolean val : values) {
            if (val != null) {
                completions.add(val.toString().toLowerCase());
            }
        }
    }

    public void addBooleans(List<Boolean> values) {
        if (values == null) return;
        for (Boolean val : values) {
            if (val != null) {
                completions.add(val.toString().toLowerCase());
            }
        }
    }

    public Completions with(String... values) {
        add(values);
        return this;
    }


    // Métodos para eliminar o limpiar
    public boolean remove(String value) {
        if (value == null) return false;
        return completions.remove(value.toLowerCase());
    }

    public void clear() {
        completions.clear();
    }
}