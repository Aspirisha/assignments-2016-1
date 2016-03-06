package ru.spbau.mit;

import java.util.ArrayList;
import java.util.List;

public class StringSetImpl implements StringSet {
    private final StringSetEntry root = new StringSetEntry(null, false);
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

    private static class StringSetEntry {
        static final int ALPHABET_SIZE = 52 + 6; // covers A-Z[...a-z

        // modifiers are useless here, but style checker is dummy
        private boolean isLastLetter;
        private final List<StringSetEntry> next = new ArrayList<>(ALPHABET_SIZE);
        private final StringSetEntry prev;
        private int howManyStartsWithThisPrefix = 0;

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
