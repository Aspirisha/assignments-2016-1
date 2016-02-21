package ru.spbau.mit;

import java.util.ArrayList;
import java.util.List;

public class StringSetImpl implements StringSet {
    private StringSetEntry root = new StringSetEntry(null, false);
    private int size = 0;


    private class StringSetEntry {
        static final int ALPHABET_SIZE = 52 + 6; // covers A-Z[...a-z

        private boolean isLastLetter;
        private List<StringSetEntry> next = new ArrayList<>(ALPHABET_SIZE);
        private StringSetEntry prev;
        private int numberOfSuccessors = 0;

        StringSetEntry(StringSetEntry prev, boolean isLastLetter) {
            this.prev = prev;
            this.isLastLetter = isLastLetter;
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                next.add(i, null);
            }
        }

        private int getIndex(char letter) {
            return letter - 'A';
        }

        StringSetEntry getNextEntry(char letter) {
            return next.get(getIndex(letter));
        }



        StringSetEntry addEntry(char letter, boolean isLastLetter) {
            StringSetEntry e = new StringSetEntry(this, isLastLetter);
            next.set(getIndex(letter), e);
            numberOfSuccessors++;
            return e;
        }

        StringSetEntry removeEntry(char letter, boolean notLastLetter) {
            numberOfSuccessors--;
            int idx = getIndex(letter);

            StringSetEntry nextEntry = next.get(idx);
            if (nextEntry.numberOfSuccessors == 1 && notLastLetter) {
                next.set(idx, null);
            }

            return nextEntry;
        }
    }


    private class Prefix {
        private StringSetEntry lastEntry;
        private int prefixLength;

        Prefix(StringSetEntry lastEntry, int prefixLength) {
            this.lastEntry = lastEntry;
            this.prefixLength = prefixLength;
        }
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

    private void removePrefix(String s) {
        StringSetEntry curEntry = root;

        for (int i = 0; i < s.length(); i++) {
            char currentLetter = s.charAt(i);
            boolean notLastLetter = i < (s.length() - 1);
            curEntry = curEntry.removeEntry(currentLetter, notLastLetter);
        }
        curEntry.isLastLetter = false;
    }

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
            se.numberOfSuccessors++;
        }

        for (; index != element.length(); index++) {
            curEntry = curEntry.addEntry(element.charAt(index), false);
        }

        curEntry.isLastLetter = true;
        return true;
    }

    // This returns true if empty string is passed
    @Override
    public boolean contains(String element) {
        if (null == element) {
            return false;
        }

        Prefix pref = findLongestPrefix(element);
        return (pref.prefixLength == element.length() && pref.lastEntry.isLastLetter);
    }

    // This returns true if empty string is passed
    @Override
    public boolean remove(String element) {
        if (!contains(element)) {
            return false;
        }

        removePrefix(element);
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

        int result = pref.lastEntry.numberOfSuccessors;

        if (pref.lastEntry.isLastLetter) {
            result += 1;
        }

        return result;
    }

}
