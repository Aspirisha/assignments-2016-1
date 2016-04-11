package sp;

public interface Function1<A, R> {
    R apply(A arg);

    default <R1> Function1<A, R1> compose(Function1<? super R, R1> g) {
        // g(f(x))
        // return arg -> g.apply(apply(arg));

        return arg -> g.apply(this.apply(arg));
    }
}
