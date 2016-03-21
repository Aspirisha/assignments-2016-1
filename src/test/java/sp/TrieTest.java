package sp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class TrieTest {

    @Test
    public void testSimple() {
        Trie stringSet = instance();

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

    @Test
    public void testSimpleSerialization() throws IOException {
        Trie trie = instance();
 
        assertTrue(trie.add("abc"));
        assertTrue(trie.add("cde"));
        assertEquals(2, trie.size());
 
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ((StreamSerializable) trie).serialize(outputStream);
 
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Trie newTrie = instance();
        ((StreamSerializable) newTrie).deserialize(inputStream);
 
        assertTrue(newTrie.contains("abc"));
        assertTrue(newTrie.contains("cde"));
        assertFalse(newTrie.contains("ab"));
        assertFalse(newTrie.contains(""));
        assertEquals(2, trie.size());
    }
    
    @Test
    public void testSerializationBigString() throws IOException {
        Trie stringSet = instance();

        Set<String> set = new HashSet<String>();
        for (int i = 0; i < 50; i++) {
            String next = generateString(50);
            assertEquals(set.add(next), stringSet.add(next));
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ((StreamSerializable) stringSet).serialize(outputStream);
        
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Trie newTrie = instance();
        ((StreamSerializable) newTrie).deserialize(inputStream);
        
        for (String s : set) {
            assertTrue(newTrie.contains(s));
        }
        
        assertEquals(set.size(), newTrie.size());
    }
    
    public static String generateString(int length) {
        Random rng = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = (char) ((char) rng.nextInt(40) + 'A');
        }
        return new String(text);
    }
 
 
  /*  @Test(expected=IOException.class)
    public void testSimpleSerializationFails() throws IOException {
        Trie trie = instance();
 
        assertTrue(trie.add("abc"));
        assertTrue(trie.add("cde"));
 
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Fail");
            }
        };
 
        ((StreamSerializable) trie).serialize(outputStream);
    }*/
 
    public static Trie instance() {
        try {
            return (Trie) Class.forName("sp.TrieImpl").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
