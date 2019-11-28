package com.vijay.jsonwizard.widgets;

import com.jsonwizard.BaseTest;
import com.jsonwizard.repository.TestMultiSelectListRepository;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.adapter.MultiSelectListAdapter;
import com.vijay.jsonwizard.adapter.MultiSelectListSelectedAdapter;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.domain.MultiSelectListAccessory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@PrepareForTest({ViewUtil.class})
public class MultiSelectListFactoryTest extends BaseTest {

    private MultiSelectListFactory multiSelectListFactory;

    @Before
    public void setUp() {
        multiSelectListFactory = new MultiSelectListFactory();
    }

    @Test
    public void prepareSelectedDataShouldReturnEmptyArrayList() {
        multiSelectListFactory.jsonObject = new JSONObject();
        Assert.assertEquals(new ArrayList<>(), multiSelectListFactory.prepareSelectedData());
    }

    @Test
    public void prepareSelectedDataShouldReturnFilledArrayList() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JsonFormConstants.VALUE,
                new JSONArray("[\n" +
                        "          {\n" +
                        "            \"key\": \"abortion\",\n" +
                        "            \"text\": \"Abortion\",\n" +
                        "            \"property\": {\n" +
                        "              \"presumed-id\": \"er\",\n" +
                        "              \"confirmed-id\": \"er\"\n" +
                        "            }\n" +
                        "          }\n" +
                        "        ]"));

        Whitebox.setInternalState(multiSelectListFactory, "jsonObject", jsonObject);

        Assert.assertEquals(1, multiSelectListFactory.prepareSelectedData().size());
    }

    @Test
    public void updateSelectedData() throws Exception {

        HashMap<String, MultiSelectListAccessory> accessoryHashMap = new HashMap<>();
        Whitebox.setInternalState(multiSelectListFactory, "multiSelectListAccessoryHashMap", accessoryHashMap);
        MultiSelectListSelectedAdapter multiSelectListAdapter = new MultiSelectListSelectedAdapter(new ArrayList<MultiSelectItem>(), multiSelectListFactory);

        MultiSelectListAccessory multiSelectListAccessory = new MultiSelectListAccessory(
                multiSelectListAdapter,
                new MultiSelectListAdapter(new ArrayList<MultiSelectItem>()),
                null,
                new ArrayList<MultiSelectItem>(),
                new ArrayList<MultiSelectItem>());
        multiSelectListFactory.currentAdapterKey = "test";

        Whitebox.invokeMethod(multiSelectListFactory, "updateMultiSelectListAccessoryHashMap", multiSelectListAccessory);
        try {
            multiSelectListFactory.updateSelectedData(new MultiSelectItem(), true);
        } catch (NullPointerException e) {
            //this exception catches call on notifyDataSetChanged since no recylclerview has been attached to the adapter;
        }
        Assert.assertEquals(1, multiSelectListFactory.getMultiSelectListSelectedAdapter().getData().size());

    }

    @Test
    public void updateListData() throws Exception {
        HashMap<String, MultiSelectListAccessory> accessoryHashMap = new HashMap<>();
        Whitebox.setInternalState(multiSelectListFactory, "multiSelectListAccessoryHashMap", accessoryHashMap);
        MultiSelectListSelectedAdapter multiSelectListAdapter = new MultiSelectListSelectedAdapter(new ArrayList<MultiSelectItem>(), multiSelectListFactory);

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

    @Test
    public void testLoadListItemsWithExternalSource() throws JSONException {
        String strJsonObject =
                "{\n" +
                        "        \"source\": \"csv\",\n" +
                        "        \"repositoryClass\": \"com.jsonwizard.repository.TestMultiSelectListRepository\"" +
                        "}";
        JSONObject jsonObject = new JSONObject(strJsonObject);
        Whitebox.setInternalState(multiSelectListFactory, "jsonObject", jsonObject);
        List<MultiSelectItem> selectItems = multiSelectListFactory.loadListItems("settings");

        Assert.assertTrue(new TestMultiSelectListRepository().fetchData().containsAll(selectItems));
    }

    @Test
    public void testLoadListItemsWithOptionsAsDataSource() throws JSONException {
        String strJsonObject =
                "{" +
                        "        \"options\": [\n" +
                        "          {\n" +
                        "            \"key\": \"Bbcess\",\n" +
                        "            \"text\": \"BAbcess\",\n" +
                        "            \"property\": {\n" +
                        "              \"presumed-id\": \"er\",\n" +
                        "              \"confirmed-id\": \"er\"\n" +
                        "            }\n" +
                        "          },\n" +
                        "          {\n" +
                        "            \"key\": \"bacterial_meningitis\",\n" +
                        "            \"text\": \"Bacterial Meningitis\",\n" +
                        "            \"property\": {\n" +
                        "              \"presumed-id\": \"er\",\n" +
                        "              \"confirmed-id\": \"er\"\n" +
                        "            }\n" +
                        "          }]"
                        + "}";
        JSONObject jsonObject = new JSONObject(strJsonObject);
        Whitebox.setInternalState(multiSelectListFactory, jsonObject, jsonObject);
        List<MultiSelectItem> selectItems = multiSelectListFactory.loadListItems(null);
        Assert.assertEquals("Bbcess", selectItems.get(0).getKey());
        Assert.assertEquals("BAbcess", selectItems.get(0).getText());

        Assert.assertEquals("bacterial_meningitis", selectItems.get(1).getKey());
        Assert.assertEquals("Bacterial Meningitis", selectItems.get(1).getText());


        Assert.assertEquals("{\"presumed-id\":\"er\",\"confirmed-id\":\"er\"}", selectItems.get(0).getValue());
        Assert.assertEquals("{\"presumed-id\":\"er\",\"confirmed-id\":\"er\"}", selectItems.get(1).getValue());

    }
}