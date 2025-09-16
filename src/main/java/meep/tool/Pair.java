package meep.tool;

import java.util.Objects;

/**
 * Simple immutable pair holding two values.
 *
 * @param <F>
 *            first type
 * @param <S>
 *            second type
 */
public final class Pair<F, S> {
    private final F first;
    private final S second;

    /**
     * Creates a new pair with the provided values.
     *
     * @param first
     *            the first value
     * @param second
     *            the second value
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first value.
     *
     * @return the first value
     */
    public F getFirst() {
        return first;
    }

    /**
     * Returns the second value.
     *
     * @return the second value
     */
    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    /**
     * Returns a string representation of this pair.
     *
     * @return a string in the form {@code Pair{first, second}}
     */
    @Override
    public String toString() {
        return "Pair{" + String.valueOf(first) + ", " + String.valueOf(second) + "}";
    }
}
