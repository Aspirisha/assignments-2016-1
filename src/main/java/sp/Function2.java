package sp;

public interface Function2<A1, A2, R> {
    R apply(A1 arg1, A2 arg2);

    default <R1> Function2<A1, A2, R1> compose(Function1<? super R, R1> g) {
        return new Function2<A1, A2, R1>() {
            @Override
            public R1 apply(A1 arg1, A2 arg2) {
                return g.apply(Function2.this.apply(arg1, arg2));
            }
        };
    }

    default Function1<A2, R> bind1(A1 arg1) {
        return new Function1<A2, R>() {

            @Override
            public R apply(A2 arg2) {
                return Function2.this.apply(arg1, arg2);
            }
        };
    }

    default Function1<A1, R> bind2(A2 arg2) {
        return new Function1<A1, R>() {

            @Override
            public R apply(A1 arg1) {
                return Function2.this.apply(arg1, arg2);
            }
        };
    }

    default Function1<A1, Function1<A2, R>> curry() {
        return new Function1<A1, Function1<A2, R>>() {

            @Override
            public Function1<A2, R> apply(A1 arg) {
                return bind1(arg);
            }
        };
    }
}
