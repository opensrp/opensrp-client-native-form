package com.vijay.jsonwizard.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.domain.MultiSelectItem;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MultiSelectListAdapterTest extends BaseTest {

    private MultiSelectListAdapter multiSelectListAdapter;

    @Before
    public void setUp() {
        multiSelectListAdapter = Mockito.spy(new MultiSelectListAdapter(new ArrayList<MultiSelectItem>()));
    }

    @Test
    public void testOnCreateViewHolder() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        RecyclerView.ViewHolder itemView = multiSelectListAdapter.onCreateViewHolder(linearLayout, 0);
        assertNotNull(itemView);
        assertTrue(itemView instanceof MultiSelectListAdapter.SectionViewHolder);


        itemView = multiSelectListAdapter.onCreateViewHolder(linearLayout, 1);
        assertNotNull(itemView);
        assertTrue(itemView instanceof MultiSelectListAdapter.SectionTitleViewHolder);
    }

    @Test
    public void testOnBindViewHolderShouldPopulateViews() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        MultiSelectItem viewSelectItem = new MultiSelectItem();
        viewSelectItem.setKey("key1");
        viewSelectItem.setText("text1");
        viewSelectItem.setValue("");

        MultiSelectItem headerSelectItem = new MultiSelectItem();
        headerSelectItem.setKey("key2");
        headerSelectItem.setText("text2");

        multiSelectListAdapter.getData().add(viewSelectItem);
        multiSelectListAdapter.getData().add(headerSelectItem);

        MultiSelectListAdapter.SectionViewHolder viewItemView = (MultiSelectListAdapter.SectionViewHolder) Mockito.spy(multiSelectListAdapter.onCreateViewHolder(linearLayout, 0));

        multiSelectListAdapter.onBindViewHolder(viewItemView, 0);

        assertEquals(viewSelectItem.getText(), viewItemView.itemTextView().getText());

        MultiSelectListAdapter.SectionTitleViewHolder viewHeaderItemView = (MultiSelectListAdapter.SectionTitleViewHolder) Mockito.spy(multiSelectListAdapter.onCreateViewHolder(linearLayout, 1));

        multiSelectListAdapter.onBindViewHolder(viewHeaderItemView, 1);

        assertEquals(headerSelectItem.getText(), viewHeaderItemView.itemHeaderView().getText());
    }
}