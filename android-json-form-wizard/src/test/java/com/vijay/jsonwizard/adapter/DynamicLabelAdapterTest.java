package com.vijay.jsonwizard.adapter;

import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.model.DynamicLabelInfo;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

public class DynamicLabelAdapterTest extends BaseTest {

    private DynamicLabelAdapter multiSelectListAdapter;

    @Before
    public void setUp() {
        multiSelectListAdapter = spy(new DynamicLabelAdapter(RuntimeEnvironment.application, new ArrayList<DynamicLabelInfo>()));
    }

    @Test
    public void testOnCreateViewHolder() {
        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        RecyclerView.ViewHolder itemView = multiSelectListAdapter.onCreateViewHolder(linearLayout, 0);
        assertNotNull(itemView);
        assertTrue(itemView instanceof DynamicLabelAdapter.RecyclerViewHolder);
    }
}