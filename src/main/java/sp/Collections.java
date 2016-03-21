package sp;

import java.util.LinkedList;
import java.util.List;

public final class Collections {

    static <A, R> List<R> map(Function1<A, R> f, Iterable<A> c) {
        List<R> output = new LinkedList<>();
        c.forEach(a->output.add(f.apply(a)));
        return output;
    }

    static <A> List<A> filter(Predicate<A> p, Iterable<A> c) {
        List<A> output = new LinkedList<>();
        c.forEach(a-> {
            if (p.apply(a)) {
                output.add(a);
            }
        });
        return output;
    }

    static <A> List<A> takeWhile(Predicate<A> p, Iterable<A> c) {
        List<A> output = new LinkedList<>();
        for (A a : c) {
            if (p.apply(a)) {
                output.add(a);
            } else {
                return output;
            }
        }

        return output;
    }

    static <A> List<A> takeUnless(Predicate<A> p, Iterable<A> c) {
        return takeWhile(p.not(), c);
    }

    static <A, R> R foldr(Function2<A, R, R> f, R ini, Iterable<A> c) {
        R result = ini;
        List<A> tmp = filter(Predicate.ALWAYS_TRUE(), c);
        java.util.Collections.reverse(tmp);
        for (A x : tmp) {
            result = f.apply(x, result);
        }
        return result;
    }

    static <A, R> R foldl(Function2<R, A, R> f, R ini, Iterable<A> c) {
        R result = ini;
        for (A x : c) {
            result = f.apply(result, x);
        }
        return result;
    }

    private Collections() {};
}
