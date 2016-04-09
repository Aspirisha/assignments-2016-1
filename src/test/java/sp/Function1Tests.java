package sp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by andy on 4/10/16.
 */
public class Function1Tests {

    @Test
    public void composeTest() {
        Function1<String, Integer> getLength = String::length;
        Function1<Object, String> objToString = Object::toString;

        String s = "This is amazing string!";
        assertEquals(getLength.compose(objToString).apply(s), Integer.toString(s.length()));
    }
}
