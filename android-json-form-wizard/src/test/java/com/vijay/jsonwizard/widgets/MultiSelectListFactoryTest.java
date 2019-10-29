package com.vijay.jsonwizard.widgets;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.adapter.MultiSelectListAdapter;
import com.vijay.jsonwizard.adapter.MultiSelectListSelectedAdapter;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.domain.MultiSelectListAccessory;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ViewUtil.class)
public class MultiSelectListFactoryTest {

    private MultiSelectListFactory multiSelectListFactory;

    @Mock
    private JSONObject jsonObject;

    @Rule
    ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        multiSelectListFactory = new MultiSelectListFactory();
    }

    @Test
    public void prepareSelectedDataShouldReturnEmptyArrayList() {
        multiSelectListFactory.jsonObject = jsonObject;
        Assert.assertEquals(new ArrayList<>(), multiSelectListFactory.prepareSelectedData());
    }

    @Test
    public void prepareSelectedDataShouldReturnFilledArrayList() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.VALUE, "[{\"key\":\"Bacterial Meningitis\",\"property\":{\"presumed-id\":\"er\",\"confirmed-id\":\"er\"}}]");
        multiSelectListFactory.jsonObject = jsonObject;
        Assert.assertEquals(1, multiSelectListFactory.prepareSelectedData().size());
    }

    @Test
    public void updateSelectedData() throws Exception {
        HashMap<String, MultiSelectListAccessory> accessoryHashMap = new HashMap<>();
        Whitebox.setInternalState(multiSelectListFactory, "multiSelectListAccessoryHashMap", accessoryHashMap);
        MultiSelectListSelectedAdapter multiSelectListAdapter = new MultiSelectListSelectedAdapter(new ArrayList<MultiSelectItem>());

        MultiSelectListAccessory multiSelectListAccessory = new MultiSelectListAccessory(
                multiSelectListAdapter,
                new MultiSelectListAdapter(new ArrayList<MultiSelectItem>()),
                null,
                new ArrayList<MultiSelectItem>(),
                new ArrayList<MultiSelectItem>());
        multiSelectListFactory.currentAdapterKey = "test";

        Whitebox.invokeMethod(multiSelectListFactory, "updateMultiSelectListAccessoryHashMap", multiSelectListAccessory);
        try {
            List<MultiSelectItem> multiSelectItems = new ArrayList<>();
            multiSelectItems.add(new MultiSelectItem());
            multiSelectListFactory.updateSelectedData(multiSelectItems, true);
        } catch (NullPointerException e) {
            //this exception catches call on notifyDataSetChanged since no recylclerview has been attached to the adapter;
        }
        Assert.assertEquals(1, multiSelectListFactory.getMultiSelectListSelectedAdapter().getData().size());

    }

    @Test
    public void updateListData() throws Exception {
        HashMap<String, MultiSelectListAccessory> accessoryHashMap = new HashMap<>();
        Whitebox.setInternalState(multiSelectListFactory, "multiSelectListAccessoryHashMap", accessoryHashMap);
        MultiSelectListSelectedAdapter multiSelectListAdapter = new MultiSelectListSelectedAdapter(new ArrayList<MultiSelectItem>());

        MultiSelectListAccessory multiSelectListAccessory = new MultiSelectListAccessory(
                multiSelectListAdapter,
                new MultiSelectListAdapter(new ArrayList<MultiSelectItem>()),
                null,
                new ArrayList<MultiSelectItem>(),
                new ArrayList<MultiSelectItem>());
        multiSelectListFactory.currentAdapterKey = "test";

        List<MultiSelectItem> multiSelectItems = new ArrayList<>();
        multiSelectItems.add(new MultiSelectItem());
        multiSelectListAccessory.setItemList(multiSelectItems);

        Whitebox.invokeMethod(multiSelectListFactory, "updateMultiSelectListAccessoryHashMap", multiSelectListAccessory);
        try {

            multiSelectListFactory.updateListData(true);
        } catch (NullPointerException e) {
            //this exception catches call on notifyDataSetChanged since no recylclerview has been attached to the adapter;
        }
        Assert.assertEquals(1, multiSelectListFactory.getMultiSelectListAdapter().getData().size());
    }
}