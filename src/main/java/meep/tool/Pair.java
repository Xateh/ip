package meep.tool;

record Pair<F, S>(F first, S second) {
    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
}
