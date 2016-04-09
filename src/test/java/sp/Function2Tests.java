package sp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by andy on 4/10/16.
 */
public class Function2Tests {

    @Test
    public void composeTest() {
        Function2<String, Integer, String> multiplexString = (str, n) ->
                new String(new char[n]).replace("\0", str);
        Function1<Object, Integer> getHashMod7 = arg -> arg.hashCode() % 7;

        assertTrue(multiplexString.compose(getHashMod7).apply("a", 45) < 7);
    }

    @Test
    public void bindTests() {
        Function2<String, Integer, String> multiplexString = (str, n) ->
                new String(new char[n]).replace("\0", str);

        String s = "poker";
        assertEquals(multiplexString.bind1(s).apply(2), s + s);
        assertEquals(multiplexString.bind2(2).apply(s), s + s);
    }

    @Test
    public void curryTest() {
        Function2<String, Integer, String> multiplexString = (str, n) ->
                new String(new char[n]).replace("\0", str);

        String s = "I hate writing tests";
        Function1<String, Function1<Integer, String>> curried = multiplexString.curry();
        assertEquals(curried.apply(s).apply(2), s + s);
    }
}
