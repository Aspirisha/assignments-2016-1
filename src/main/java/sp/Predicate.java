package sp;

public interface Predicate<A> extends Function1<A, Boolean> {
    Predicate<Object> always_true = arg -> true;

    Predicate<Object> always_false = arg -> false;

    default Predicate<A> or(Predicate<? super A> other) {
        return arg -> Predicate.this.apply(arg) || other.apply(arg);
    }

    default Predicate<A> and(Predicate<A> other) {
        return arg -> Predicate.this.apply(arg) && other.apply(arg);
    }

    default Predicate<A> not() {
        return arg -> !Predicate.this.apply(arg);
    }
}
