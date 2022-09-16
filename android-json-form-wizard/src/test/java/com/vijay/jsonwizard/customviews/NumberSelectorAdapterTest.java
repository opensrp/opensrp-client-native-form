package com.vijay.jsonwizard.customviews;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

public class NumberSelectorAdapterTest extends BaseTest {
    @Mock
    private View view;
    @Mock

    private Context context;
    private final List<String> numbers = List.of("1", "2", "3", "4");
    private NumberSelectorAdapter numberSelectorAdapter;


    @Before
    public void setUp() {
        context = RuntimeEnvironment.application.getApplicationContext();
        view = Mockito.mock(View.class);
        numberSelectorAdapter = new NumberSelectorAdapter(context, numbers);

    }

    @Test
    public void testGetCount() {
        int numberCount = numbers.size();
        Assert.assertEquals(numberCount, numberSelectorAdapter.getCount());
    }

    @Test
    public void testGetItem() {
        Object position = numbers.get(1);
        Assert.assertEquals(position, numberSelectorAdapter.getItem(1));
    }

    @Test
    public void testGetItemId() {
        long position = Long.parseLong(numbers.get(1));
        Assert.assertEquals(position, numberSelectorAdapter.getItemId(1));
    }

    @Test
    public void testGetView() {
        TextView textView=new TextView(context);
        textView.setTag((Object) numbers.get(1));
        Mockito.when(view.findViewById(R.id.selected_number)).thenReturn(textView);
        Object o=numbers.get(1);
        view.setTag(o);
        NumberSelectorAdapter.ViewHolder viewHolder=new NumberSelectorAdapter.ViewHolder(view);
        Mockito.when(view.getTag()).thenReturn(viewHolder);
        int position = 1;
        ViewGroup viewGroup = Mockito.mock(ViewGroup.class);
        View returnView = numberSelectorAdapter.getView(position, view, viewGroup);
        Assert.assertNotNull(returnView);

    }

}
