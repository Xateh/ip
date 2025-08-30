package meep.tool;

/**
 * Simple immutable pair record holding two values.
 * @param <F> first type
 * @param <S> second type
 */
record Pair<F, S>(F first, S second) {
     /**
      * Returns the first value.
      * @return first
      */
    public F getFirst() {
        return first;
    }

     /**
      * Returns the second value.
      * @return second
      */
    public S getSecond() {
        return second;
    }
}
