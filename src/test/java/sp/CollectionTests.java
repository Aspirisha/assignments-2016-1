package sp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import com.google.common.primitives.Ints;

import junitx.framework.ListAssert;

public class CollectionTests {

    public List<Integer> generateIntegerList(int size) {
        List<Integer> randomNumbers = new ArrayList<>();
        Random gen = new Random();

        for (int i = 0; i < size; i++) {
            randomNumbers.add(gen.nextInt(1000));
        }

        return randomNumbers;
    }

    @Test
    public void testFilter() {
        int[] a = {3, 14, 15, 92, 65};
        List<Integer> l = Ints.asList(a);

        List<Integer> filtered = Collections.filter(new Predicate<Integer>() {

            @Override
            public Boolean apply(Integer arg) {
                return arg > 7;
            }
        }, l);


        int[] ans = Arrays.stream(a).filter(x -> x > 7).toArray();
        assertEquals(ans.length, filtered.size());
        for (int i = 0; i < ans.length; i++) {
            assertEquals(ans[i], (int)filtered.get(i));
        }
    }

    @Test
    public void testMap() {
        int size = 100;
        List<Integer> randomNumbers = generateIntegerList(size);
        List<Integer> mapped = Collections.map(new Function1<Integer, Integer>() {

            @Override
            public Integer apply(Integer arg) {
                return arg * arg;
            }
        }, randomNumbers);

        for (int i = 0; i < size; i++) {
            assertEquals(randomNumbers.get(i) * randomNumbers.get(i), (int)mapped.get(i));
        }
    }

    @Test
    public void testTakeWhile() {
        int size = 100;
        List<Integer> randomNumbers = generateIntegerList(size);
        List<Integer> filtered = Collections.takeWhile(new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer arg) {
                return arg % 7 != 0;
            }
        }, randomNumbers);

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
        List<Integer> filtered = Collections.takeWhile(Predicate.<Integer>ALWAYS_TRUE().or(new Predicate<Integer>() {

            @Override
            public Boolean apply(Integer arg) {
                assert(false); // lazyness should save us
                return false;
            }
        }), randomNumbers);

        assertEquals(size, filtered.size());

        filtered = Collections.filter(new Predicate<Integer>() {

            @Override
            public Boolean apply(Integer arg) {
                return arg % 7 == 3;
            }
        }.or(new Predicate<Integer>() {

            @Override
            public Boolean apply(Integer arg) {
                return arg % 7 == 5;
            }
        }), randomNumbers);

        for (int x : filtered) {
            assertTrue(x % 7 == 5 || x % 7 == 3);
        }
    }
}
