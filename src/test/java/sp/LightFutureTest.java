package sp;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by andy on 5/8/16.
 */
public class LightFutureTest {

    static Supplier<List<Integer>> getAllPrimesLessThan(int n) {
        return new Supplier<List<Integer>>() {

            @Override
            public List<Integer> get() {
                ArrayList<Integer> result = new ArrayList<>();
                result.add(2);

                for (int i = 3; i < n; i++) {
                    gotofun:
                    {
                        for (int j = 2; j < Math.ceil(Math.sqrt(i)) + 1; j++) {
                            if (i % j == 0) {
                                break gotofun;
                            }
                        }
                        result.add(i);
                    }
                }
                return result;
            }
        };
    }

    @Test
    public void simpleTest() throws LightFuture.LightExecutionException {
        int N = 1000;

        LightFuture<List<Integer>> primeNumbers = new LightFutureImpl<>(getAllPrimesLessThan(N), null);
        assertTrue(primeNumbers.get().contains(47));
    }

    @Test
    public void thenApplyTest() throws LightFuture.LightExecutionException {
        int N = 1000;

        ThreadPool tp = new ThreadPool(2);
        LightFuture<List<Integer>> primeNumbers = new LightFutureImpl<>(getAllPrimesLessThan(N), tp);
        LightFuture<List<Integer>> primesGreaterThanM = primeNumbers.thenApply(lst -> lst.stream().filter(x -> x > 10).collect(Collectors.toList()));
        List<Integer> result = primeNumbers.get().stream().filter(x -> x > 10).collect(Collectors.toList());
        assertEquals(result, primesGreaterThanM.get());
    }
}
