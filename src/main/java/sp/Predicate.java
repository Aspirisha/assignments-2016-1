package sp;

public abstract class Predicate<A> implements Function1<A, Boolean> {
    static <A> Predicate<A> ALWAYS_TRUE() {
        return new Predicate<A>() {
            @Override
            public Boolean apply(Object arg) {
                return true;
            }
        };
    }

    static <A> Predicate<A> ALWAYS_FALSE() {
        return new Predicate<A>() {
            @Override
            public Boolean apply(Object arg) {
                return false;
            }
        };
    }

    Predicate<A> or(Predicate<A> other) {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A arg) {
                if (!Predicate.this.apply(arg)) {
                    return other.apply(arg);
                }
                return true;
            }
        };
    }

    Predicate<A> and(Predicate<A> other) {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A arg) {
                if (!Predicate.this.apply(arg)) {
                    return false;
                }
                return other.apply(arg);
            }
        };
    }

    Predicate<A> not() {
        return new Predicate<A>() {
            @Override
            public Boolean apply(A arg) {
               return !Predicate.this.apply(arg);
            }
        };
    }

}
