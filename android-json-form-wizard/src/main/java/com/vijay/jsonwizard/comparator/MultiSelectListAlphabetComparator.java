package com.vijay.jsonwizard.comparator;

import com.vijay.jsonwizard.domain.MultiSelectItem;

import java.util.Comparator;

public class MultiSelectListAlphabetComparator implements Comparator<MultiSelectItem> {

    @Override
    public int compare(MultiSelectItem o1, MultiSelectItem o2) {
        return o1.getKey().toLowerCase().compareTo(o2.getKey().toLowerCase());
    }
}
