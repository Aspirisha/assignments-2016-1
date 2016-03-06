package ru.spbau.mit;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StringSetTest {

    @Test
    public void testSimple() {
        StringSet stringSet = instance();

        assertTrue(stringSet.add("abc"));
        assertTrue(stringSet.contains("abc"));
        assertEquals(1, stringSet.size());
        assertEquals(1, stringSet.howManyStartsWithPrefix("abc"));
        
        assertFalse(stringSet.contains(""));
        assertFalse(stringSet.contains("dee"));
        assertFalse(stringSet.add("abc"));
        assertTrue(stringSet.add("abcd"));
        assertTrue(stringSet.contains("abcd"));
        assertTrue(stringSet.remove("abc"));
        assertTrue(stringSet.contains("abcd"));
        assertTrue(stringSet.add(""));
        assertTrue(stringSet.contains(""));
        assertFalse(stringSet.contains("ab"));
        assertTrue(stringSet.add("ab"));
        assertTrue(stringSet.contains("ab"));
        assertFalse(stringSet.add("ab"));
        assertTrue(stringSet.contains("ab"));
        
        assertTrue(stringSet.add("Zz"));
        assertTrue(stringSet.contains("ab"));
        assertFalse(stringSet.remove("dedefefeg"));
        assertTrue(stringSet.contains("ab"));
        assertTrue(stringSet.remove("ab"));
        
        assertFalse(stringSet.remove("ab"));

        assertFalse(stringSet.add("Zz"));
        assertFalse(stringSet.remove("Z"));
        assertTrue(stringSet.remove("Zz"));
        assertFalse(stringSet.remove("Zz"));
        
        assertFalse(stringSet.add(""));
        assertTrue(stringSet.remove(""));
        assertFalse(stringSet.contains(""));
        assertTrue(stringSet.add(""));
        
        assertEquals(2, stringSet.size());
        assertEquals(2, stringSet.howManyStartsWithPrefix(""));
        
        assertEquals(0, stringSet.howManyStartsWithPrefix("sa"));
    }

    public static StringSet instance() {
        try {
            return (StringSet) Class.forName("ru.spbau.mit.StringSetImpl").newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new IllegalStateException("Error while class loading");
    }
}
