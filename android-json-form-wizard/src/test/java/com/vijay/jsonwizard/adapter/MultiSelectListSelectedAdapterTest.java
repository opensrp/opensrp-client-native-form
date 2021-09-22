package com.vijay.jsonwizard.adapter;

import android.widget.LinearLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.widgets.MultiSelectListFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class MultiSelectListSelectedAdapterTest extends BaseTest {

    private MultiSelectListSelectedAdapter multiSelectListSelectedAdapter;

    private MultiSelectListFactory multiSelectListFactory;

    @Before
    public void setUp() {
        multiSelectListFactory = spy(new MultiSelectListFactory());
        multiSelectListSelectedAdapter = spy(new MultiSelectListSelectedAdapter(new ArrayList<MultiSelectItem>(), "",
                multiSelectListFactory));
    }

    @Test
    public void testOnCreateViewHolderShouldNotReturnNull() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        MultiSelectListSelectedAdapter.MyViewHolder itemView = multiSelectListSelectedAdapter.onCreateViewHolder(linearLayout, 0);
        assertNotNull(itemView);
    }

    @Test
    public void testOnBindViewHolderShouldPopulateViews() throws JSONException {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);

        JSONObject metaJsonObject = new JSONObject();
        metaJsonObject.put(JsonFormConstants.MultiSelectUtils.INFO, "info");

        JSONObject valueJsonObject = new JSONObject();
        valueJsonObject.put(JsonFormConstants.MultiSelectUtils.META, metaJsonObject);

        MultiSelectItem multiSelectItem = new MultiSelectItem();
        multiSelectItem.setKey("key");
        multiSelectItem.setText("text");
        multiSelectItem.setValue(valueJsonObject.toString());
        multiSelectListSelectedAdapter.getData().add(multiSelectItem);

        doNothing().when(multiSelectListFactory).writeToForm("");
        MultiSelectListSelectedAdapter.MyViewHolder myViewHolder = spy(multiSelectListSelectedAdapter.onCreateViewHolder(linearLayout, 0));
        multiSelectListSelectedAdapter.onBindViewHolder(myViewHolder, 0);

        assertEquals(multiSelectItem.getText(), myViewHolder.itemTextView().getText());
        assertEquals("info", myViewHolder.additionalInfoView().getText());

        myViewHolder.deleteImageView().performClick();

        assertEquals(0, multiSelectListSelectedAdapter.getData().size());

        verify(multiSelectListFactory, only()).writeToForm("");
    }
}