package sp;

import junitx.framework.ListAssert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CollectionTests {

    private static List<Integer> generateIntegerList(int size) {
        List<Integer> randomNumbers = new ArrayList<>();
        Random gen = new Random();

        for (int i = 0; i < size; i++) {
            randomNumbers.add(gen.nextInt(1000));
        }

        return randomNumbers;
    }

    @Test
    public void testFilter() {
        List<Integer> l = Arrays.asList(3, 14, 15, 92, 65);
        List<Integer> filtered = Collections.filter(arg -> arg > 7, l);
        List<Integer> ans = l.stream().filter(x -> x > 7).collect(Collectors.toList());

        assertEquals(ans, filtered);
    }

    @Test
    public void testMap() {
        int size = 100;
        List<Integer> randomNumbers = generateIntegerList(size);
        List<Integer> mapped = Collections.map(arg -> arg * arg, randomNumbers);

        for (int i = 0; i < size; i++) {
            assertEquals(randomNumbers.get(i) * randomNumbers.get(i), (int)mapped.get(i));
        }
    }

    @Test
    public void testTakeWhile() {
        int size = 100;
        List<Integer> randomNumbers = generateIntegerList(size);
        List<Integer> filtered = Collections.takeWhile(arg -> arg % 7 != 0, randomNumbers);

        List<Integer> reference = new ArrayList<>();
        for (int x : randomNumbers) {
            if (x % 7 != 0) {
                reference.add(x);
            } else {
                break;
            }
        }
        ListAssert.assertEquals(reference, filtered);
    }

    @Test 
    public void testOr() {
        int size = 1000;
        List<Integer> randomNumbers = generateIntegerList(size);
        List<Integer> filtered = Collections.takeWhile(Predicate.always_true.or(arg -> {
            assert (false); // lazyness will save us
            return false;
        }), randomNumbers);

        assertEquals(size, filtered.size());

        filtered = Collections.filter(((Predicate<Integer>)arg -> arg % 7 == 3).
                or(arg -> arg % 7 == 5), randomNumbers);

        for (int x : filtered) {
            assertTrue(x % 7 == 5 || x % 7 == 3);
        }
    }

    @Test
    public void testIntsFunc() {
        final List<Integer> c1 = Arrays.asList(1, 2, 3);
        assertEquals(Collections.map((Function1<Object, String>) Object::toString, c1),
                c1.stream().map(Object::toString).collect(Collectors.toList()));
    }


    @Test
    public void testFolds() {
        Function2<Integer, Integer, Integer> prodMod47 = (a, b) -> a * b % 47;
        assertEquals(0, (long)Collections.foldl(prodMod47, 1, Arrays.asList(1,2,3,47)));
        assertEquals(6*43 % 47, (long)Collections.foldl(prodMod47, 1, Arrays.asList(1,2,3,43)));
        assertEquals((long)Collections.foldl(prodMod47, 1, Arrays.asList(1,2,3,47)),
                (long)Collections.foldr(prodMod47, 1, Arrays.asList(1,2,3,47)));
    }
}
