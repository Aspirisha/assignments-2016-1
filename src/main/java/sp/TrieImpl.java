package sp;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

public class TrieImpl implements Trie, StreamSerializable {
    private static final int MAGIC_NUMBER = 314159265;
    private StringSetEntry root = new StringSetEntry(null, false);
    private int size = 0;

    @Override
    public boolean add(String element) {
        if (null == element) {
            return false;
        }

        Prefix pref = findLongestPrefix(element);
        int index = pref.prefixLength;
        StringSetEntry curEntry = pref.lastEntry;

        if (index == element.length() && curEntry.isLastLetter) {
            return false;
        }

        size++;
        for (StringSetEntry se = curEntry.prev; se != null; se = se.prev) {
            se.howManyStartsWithThisPrefix++;
        }

        for (; index != element.length(); index++) {
            curEntry = curEntry.addEntry(element.charAt(index), false);
        }

        curEntry.isLastLetter = true;
        return true;
    }


    @Override
    public boolean contains(String element) {
        if (null == element) {
            return false;
        }

        Prefix pref = findLongestPrefix(element);
        return (pref.prefixLength == element.length() && pref.lastEntry.isLastLetter);
    }

    @Override
    public boolean remove(String element) {
        if (null == element) {
            return false;
        }
        Prefix pref = findLongestPrefix(element);
        if (pref.prefixLength != element.length() || !pref.lastEntry.isLastLetter) {
            return false;
        }

        StringSetEntry curEntry = pref.lastEntry.prev;
        pref.lastEntry.isLastLetter = false;
        int index = element.length() - 1;
        while (curEntry != null) {
            curEntry.removeEntry(element.charAt(index--));
            curEntry = curEntry.prev;
        }

        size--;
        return true;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int howManyStartsWithPrefix(String prefix) {
        if (null == prefix) {
            return 0;
        }

        Prefix pref = findLongestPrefix(prefix);

        if (pref.prefixLength != prefix.length()) {
            return 0;
        }

        int result = pref.lastEntry.howManyStartsWithThisPrefix;

        if (pref.lastEntry.isLastLetter) {
            result += 1;
        }

        return result;
    }


    @Override
    public void serialize(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        Queue<SerializationQueueElement> q = new LinkedList<>();
        int id = 0;
        q.add(new SerializationQueueElement(id, -1, (char) 0, root));
        JSONArray dump = new JSONArray();

        while (!q.isEmpty()) {
            SerializationQueueElement cur = q.poll();
            dump.put(cur.serialize());
            for (int i = 0; i < cur.sse.next.size(); i++) {
                StringSetEntry sse = cur.sse.next.get(i);
                if (null == sse) {
                    continue;
                }
                id++;
                q.add(new SerializationQueueElement(id, cur.id, i, sse));
            }
        }
        
        JSONObject metaObj = new JSONObject();
        metaObj.put("array_size", id + 1);
        metaObj.put("array", dump);
        metaObj.put("words_number", size);
        metaObj.put("magic", MAGIC_NUMBER);
        dos.writeUTF(metaObj.toString());
        dos.close();
    }


    @Override
    public void deserialize(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);

        JSONObject metaObj = new JSONObject(dis.readUTF());
        int magicNumber = metaObj.getInt("magic");

        if (magicNumber != MAGIC_NUMBER) {
            throw new IOException("Input stream doesn't contain serialization of Trie object");
        }

        JSONArray dump = metaObj.getJSONArray("array");
        int arraySize = metaObj.getInt("array_size");
        HashMap<Integer, StringSetEntry> entries = new HashMap<>();
        for (int i = 0; i < arraySize; i++) {
            JSONObject obj = dump.getJSONObject(i);
            StringSetEntry.deserialize(entries, obj);
        }
        root = entries.get(0);
        size = metaObj.getInt("words_number");
        dis.close();
    }

    private Prefix findLongestPrefix(String s) {
        int index = 0;
        StringSetEntry nextEntry = root;
        StringSetEntry curEntry = root;

        for (; nextEntry != null && index != s.length(); index++) {
            curEntry = nextEntry;
            nextEntry = nextEntry.getNextEntry(s.charAt(index));
        }

        if (nextEntry == null) {
            index--;
        } else {
            curEntry = nextEntry;
        }

        return new Prefix(curEntry, index);
    }

    private class SerializationQueueElement {
        private final int id;
        private final int parentId;
        private final int index;
        private final StringSetEntry sse;

        SerializationQueueElement(int id, int parentId, int index, StringSetEntry sse) {
            this.id = id;
            this.parentId = parentId;
            this.index = index;
            this.sse = sse;
        }

        JSONObject serialize() {
            return sse.serialize(parentId, id, index);
        }
    }

    private static class StringSetEntry {

        static final int ALPHABET_SIZE = 52 + 6; // covers A-Z[...a-z

        // modifiers are useless here, but style checker is dummy
        private boolean isLastLetter;
        private final List<StringSetEntry> next = new ArrayList<>(ALPHABET_SIZE);
        private final StringSetEntry prev;
        private int howManyStartsWithThisPrefix = 0;

        private JSONObject serialize(int parentId, Integer id, int index) {
            JSONObject obj = new JSONObject();
            obj.put("parent_id", parentId);
            obj.put("id", id);
            obj.put("is_last_letter", isLastLetter);
            obj.put("index", index);
            obj.put("How_many_starts_prefix", howManyStartsWithThisPrefix);
            return obj;
        }

        private static StringSetEntry deserialize(HashMap<Integer, StringSetEntry> entries, JSONObject obj) {
            int id = obj.getInt("id");
            int parentId = obj.getInt("parent_id");
            StringSetEntry parent = entries.containsKey(parentId) ? entries.get(parentId) : null;
            StringSetEntry tmp = new StringSetEntry(parent, obj.getBoolean("is_last_letter"));
            if (null != parent) {
                parent.next.set(obj.getInt("index"), tmp);
            }
            tmp.howManyStartsWithThisPrefix = obj.getInt("How_many_starts_prefix");
            entries.put(id, tmp);
            return tmp;
        }

        StringSetEntry(StringSetEntry prev, boolean isLastLetter) {
            this.prev = prev;
            this.isLastLetter = isLastLetter;
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                next.add(i, null);
            }
        }

        static int getIndex(char letter) {
            return letter - 'A';
        }

        StringSetEntry getNextEntry(char letter) {
            return next.get(getIndex(letter));
        }

        StringSetEntry addEntry(char letter, boolean isLastLetter) {
            StringSetEntry e = new StringSetEntry(this, isLastLetter);
            next.set(getIndex(letter), e);
            howManyStartsWithThisPrefix++;
            return e;
        }

        void removeEntry(char letter) {
            howManyStartsWithThisPrefix--;
            int idx = getIndex(letter);

            StringSetEntry nextEntry = next.get(idx);
            if (nextEntry.howManyStartsWithThisPrefix == 0) {
                next.set(idx, null);
            }
        }
    }


    private class Prefix {
        private final StringSetEntry lastEntry;
        private int prefixLength;

        Prefix(StringSetEntry lastEntry, int prefixLength) {
            this.lastEntry = lastEntry;
            this.prefixLength = prefixLength;
        }
    }
}
