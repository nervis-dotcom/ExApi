package ex.nervisking.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

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

    /**
     * Agrega una cantidad específica de números consecutivos a completions.
     *
     * @param cantidad número de elementos a agregar (desde 1 hasta cantidad)
     */
    public void addConsecutive(int cantidad) {
        if (cantidad <= 0) return;
        for (int i = 1; i <= cantidad; i++) {
            completions.add(String.valueOf(i));
        }
    }

    /**
     * Agrega los nombres de todos los jugadores en línea a completions.
     */
    public void addPlayerOnline() {
        add(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
    }

    /**
     * Agrega jugadores en línea que cumplan una condición personalizada.
     *
     * @param filter Filtro para determinar qué jugadores agregar.
     */
    public void addPlayerOnline(Predicate<Player> filter) {
        if (filter == null) return;
        Bukkit.getOnlinePlayers().stream()
                .filter(filter)
                .map(Player::getName)
                .forEach(name -> completions.add(name.toLowerCase()));
    }

    /**
     * Agrega jugadores en línea que cumplan una condición personalizada.
     * Versión encadenable para usar en cadena de métodos.
     *
     * @param filter Filtro para determinar qué jugadores agregar.
     * @return esta misma instancia de Completions.
     */
    public Completions withPlayerOnline(Predicate<Player> filter) {
        if (filter != null) {
            Bukkit.getOnlinePlayers().stream()
                    .filter(filter)
                    .map(Player::getName)
                    .forEach(name -> completions.add(name.toLowerCase()));
        }
        return this;
    }

    /**
     * Agrega valores de completion si se cumple una condición.
     *
     * @param condition Condición que debe cumplirse.
     * @param values    Valores a agregar si la condición es verdadera.
     */
    public void addIf(boolean condition, List<String> values) {
        if (condition && values != null) {
            add(values);
        }
    }

    public Completions withIf(boolean condition, List<String> values) {
        addIf(condition, values);
        return this;
    }

    public void addIf(boolean condition, String... values) {
        this.addIf(condition, List.of(values));
    }

    public Completions withIf(boolean condition, String... values) {
        return this.withIf(condition, List.of(values));
    }

    /**
     * Agrega valores booleanos comunes a completions.
     * Incluye "true" y "false".
     */
    public void addBooleanValues() {
        add("true", "false");
    }

    /**
     * Filtra las completions y deja solo las que comienzan con el prefijo dado.
     *
     * @param prefix Prefijo a filtrar (case-insensitive).
     */
    public void filterStartsWith(String prefix) {
        if (prefix == null || prefix.isBlank()) return;
        String lower = prefix.toLowerCase();
        completions.removeIf(s -> !s.startsWith(lower));
    }

    /**
     * Filtra las completions usando una condición personalizada.
     *
     * @param condition Predicate que define si un elemento debe mantenerse.
     */
    public void filter(Predicate<String> condition) {
        if (condition == null) return;
        completions.removeIf(condition.negate());
    }
}