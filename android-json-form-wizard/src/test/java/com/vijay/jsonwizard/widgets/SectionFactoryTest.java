package com.vijay.jsonwizard.widgets;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;

public class SectionFactoryTest extends FactoryTest {

    private SectionFactory factory;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        factory = new SectionFactory();
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        SectionFactory factorySpy = Mockito.spy(factory);

        Set<String> editableProperty = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(new HashSet<>(), editableProperty);
    }
}
