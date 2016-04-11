package sp;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class Collections {

    public static <A, R> List<R> map(Function1<? super A, R> f, Iterable<A> c) {
        List<R> output = new LinkedList<>();
        c.forEach(a->output.add(f.apply(a)));
        return output;
    }

    public static <A> List<A> filter(Predicate<? super A> p, Iterable<A> c) {
        return StreamSupport.stream(c.spliterator(), false).
                filter(p::apply).
                collect(Collectors.toList());
    }

    public static <A> List<A> takeWhile(Predicate<? super A> p, Iterable<A> c) {
        List<A> output = new LinkedList<>();
        for (A a : c) {
            if (!p.apply(a)) {
                break;
            }
            output.add(a);
        }

        return output;
    }

    public static <A> List<A> takeUnless(Predicate<? super A> p, Iterable<A> c) {
        return takeWhile(p.not(), c);
    }

    public static <A, R> R foldr(Function2<? super A, R, R> f, R ini, Iterable<A> c) {
        R result = ini;
        List<A> tmp = StreamSupport.stream(c.spliterator(), false).collect(Collectors.toList());
        java.util.Collections.reverse(tmp);
        for (A x : tmp) {
            result = f.apply(x, result);
        }
        return result;
    }

    public static <A, R> R foldl(Function2<R, ? super A, R> f, R ini, Iterable<A> c) {
        R result = ini;
        for (A x : c) {
            result = f.apply(result, x);
        }
        return result;
    }

    private Collections() {}
}
