package sp;

import com.google.common.collect.ImmutableList;
import jdk.nashorn.internal.ir.annotations.Immutable;
import junitx.framework.ListAssert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static sp.CollectionTests.generateIntegerList;

/**
 * Created by andy on 4/9/16.
 */
public class PredicateTests {
    @Test
    public void testPredicate() {
        Predicate<String> isEmptyPredicate = String::isEmpty;

        assertTrue(isEmptyPredicate.apply(""));
        assertFalse(isEmptyPredicate.apply("not empty"));
    }

    @Test
    public void testOr() {
        Predicate<String> isEmptyPredicate = String::isEmpty;
        Predicate<String> hasLetterA = arg -> arg.matches(".*(a|A).*");
        Predicate<String> emptyOrWithA = hasLetterA.or(isEmptyPredicate);

        assertTrue(emptyOrWithA.apply(""));
        assertFalse(emptyOrWithA.apply("misteke"));
        assertTrue(emptyOrWithA.apply("mistake"));

        // wildcards tests
        Predicate<Object> isNotNull = arg -> arg != null;
        assertTrue(hasLetterA.or(isNotNull).apply("misteke"));

        Predicate<String> toBe = arg -> arg.toLowerCase().equals("bb");
        Predicate<String> notToBe = arg -> !arg.toLowerCase().equals("bb");

        assertFalse(toBe.or(toBe).apply("two beer"));
        assertTrue(toBe.or(notToBe).apply("two beer"));
    }

    @Test
    public void testAnd() {
        Predicate<String> hasEvenLength = arg -> (arg.length() & 1) == 0;
        Predicate<String> hasLetterA = arg -> arg.matches(".*(a|A).*");
        Predicate<String> evenLengthAndWithA = hasLetterA.and(hasEvenLength);
        Predicate<Object> isNotNull = arg -> arg != null;


        assertFalse(evenLengthAndWithA.apply(""));
        assertFalse(evenLengthAndWithA.apply("misteke"));
        assertFalse(evenLengthAndWithA.apply("mistake"));
        assertTrue(evenLengthAndWithA.apply("mistake!"));
        assertTrue(evenLengthAndWithA.and(isNotNull).apply("mistake!"));

    }

    @Test
    public void testNot() {
        Predicate<String> isEmptyPredicate = String::isEmpty;

        assertFalse(isEmptyPredicate.not().apply(""));
        assertTrue(isEmptyPredicate.not().apply("not empty"));
    }

    @Test
    public void testConstantPredicates() {
        assertFalse(Predicate.ALWAYS_FALSE.apply(""));
        assertTrue(Predicate.ALWAYS_TRUE.apply(false));

        Predicate<Object> dontCallMe = obj -> {
            assertTrue(false);
            return true;
        };

        //lazyness
        assertTrue(Predicate.ALWAYS_TRUE.or(dontCallMe).apply(""));
        assertFalse(Predicate.ALWAYS_FALSE.and(dontCallMe).apply(""));
    }

    @Test
    public void testTakeWhileUnless() {
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

        filtered = Collections.takeUnless(arg -> arg % 7 == 0, randomNumbers);
        ListAssert.assertEquals(reference, filtered);

        assertTrue(Collections.takeUnless(arg -> arg % 7 == 0,
                ImmutableList.<Integer>of()).isEmpty());
        assertTrue(Collections.takeWhile(arg -> arg % 7 == 0,
                ImmutableList.<Integer>of()).isEmpty());
    }

}
