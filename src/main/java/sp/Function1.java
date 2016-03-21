package sp;

public interface Function1<A, R> {
    R apply(A arg);

    default <R1> Function1<A, R1> compose(Function1<? super R, R1> g) {
        // g(f(x))
        // return arg -> g.apply(apply(arg));
        return new Function1<A, R1>() {
            @Override
            public R1 apply(A arg) {
                return g.apply(Function1.this.apply(arg));
            }
        };
    }
}
