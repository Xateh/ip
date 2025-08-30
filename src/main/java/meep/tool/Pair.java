package meep.tool;

/**
 * Simple immutable container of two values.
 *
 * @param <F> type of first
 * @param <S> type of second
 */
record Pair<F, S>(F first, S second) {
     /**
      * Returns the first value.
      *
      * @return first component
      */
    public F getFirst() {
        return first;
    }

     /**
      * Returns the second value.
      *
      * @return second component
      */
    public S getSecond() {
        return second;
    }
}
