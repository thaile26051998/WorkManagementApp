package com.hcmus.easywork.utils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Used for sorting List by name
 */
public class CustomizedCollections {
    public static <T> void sort(List<T> list, CollatorComparator<? super T> c) {
        Collections.sort(list, c);
    }

    public static abstract class CollatorComparator<T> implements Comparator<T> {
        final static Collator collator = Collator.getInstance(new Locale("vi-VN"));

        protected int compareTo(String s1, String s2) {
            return collator.compare(s1, s2);
        }
    }

    public static <T> List<T> filter(List<T> list, ItemFilter<T> filter) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (filter.isMatch(item))
                result.add(item);
        }
        return result;
    }

    public interface ItemFilter<T> {
        boolean isMatch(T object);
    }
}
