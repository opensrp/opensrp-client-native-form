package com.vijay.jsonwizard.comparator;

import com.vijay.jsonwizard.domain.MultiSelectItem;

import org.junit.Assert;
import org.junit.Test;

public class MultiSelectListAlphabetComparatorTest {

    @Test
    public void testCompare() {
        MultiSelectItem multiSelectItem1 = new MultiSelectItem();
        multiSelectItem1.setKey("key1");
        MultiSelectItem multiSelectItem2 = new MultiSelectItem();
        multiSelectItem2.setKey("key2");

        MultiSelectListAlphabetComparator comparator = new MultiSelectListAlphabetComparator();
        Assert.assertEquals(-1, comparator.compare(multiSelectItem1, multiSelectItem2));
        Assert.assertEquals(1, comparator.compare(multiSelectItem2, multiSelectItem1));
        Assert.assertEquals(0, comparator.compare(multiSelectItem1, multiSelectItem1));
    }
}
