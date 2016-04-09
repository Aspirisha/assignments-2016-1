package sp;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

        assertFalse(evenLengthAndWithA.apply(""));
        assertFalse(evenLengthAndWithA.apply("misteke"));
        assertFalse(evenLengthAndWithA.apply("mistake"));
        assertTrue(evenLengthAndWithA.apply("mistake!"));
    }

    @Test
    public void testNot() {
        Predicate<String> isEmptyPredicate = String::isEmpty;

        assertFalse(isEmptyPredicate.not().apply(""));
        assertTrue(isEmptyPredicate.not().apply("not empty"));
    }

    @Test
    public void testConstantPredicates() {
        assertFalse(Predicate.always_false.apply(""));
        assertTrue(Predicate.always_true.apply(false));
    }
}
