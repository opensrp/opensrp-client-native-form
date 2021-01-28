package com.vijay.jsonwizard.listeners;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;

public class ExpansionPanelRecordButtonClickListenerTest extends BaseTest {

    @Mock
    private LinearLayout mockLinearLayout;

    private ExpansionPanelRecordButtonClickListener panelRecordButtonClickListener;

    @Before
    public void setUp() {
        panelRecordButtonClickListener = Mockito.spy(new ExpansionPanelRecordButtonClickListener());
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnClickShouldInvokeRequiredMethods() {
        LinearLayout view = Mockito.spy(new LinearLayout(RuntimeEnvironment.application));
        view.setTag(R.id.type, JsonFormConstants.BUTTON);

        Mockito.doReturn(mockLinearLayout).when(view).getParent();
        Mockito.doReturn(mockLinearLayout).when(mockLinearLayout).getParent();

        RelativeLayout mockLayoutHeader = Mockito.mock(RelativeLayout.class);
        Mockito.doReturn(mockLayoutHeader).when(mockLinearLayout).getChildAt(0);

        LinearLayout mockContentLayout = Mockito.mock(LinearLayout.class);
        Mockito.doReturn(mockContentLayout).when(mockLinearLayout).getChildAt(1);

        RelativeLayout expansionHeaderLayout = Mockito.spy(new RelativeLayout(view.getContext()));
        Mockito.doReturn(expansionHeaderLayout).when(mockLayoutHeader).findViewById(R.id.expansion_header_layout);

        ImageView statusImageView = Mockito.spy(new ImageView(view.getContext()));
        Mockito.doReturn(statusImageView).when(expansionHeaderLayout).findViewById(R.id.statusImageView);

        CustomTextView topBarTextView = Mockito.spy(new CustomTextView(view.getContext()));
        Mockito.doReturn(topBarTextView).when(expansionHeaderLayout).findViewById(R.id.topBarTextView);

        LinearLayout buttonLayout = Mockito.spy(new LinearLayout(view.getContext()));
        Mockito.doReturn(buttonLayout).when(mockContentLayout).findViewById(R.id.accordion_bottom_navigation);

        Button okButton = new Button(view.getContext());
        Mockito.doReturn(okButton).when(buttonLayout).findViewById(R.id.ok_button);

        ReflectionHelpers.setField(panelRecordButtonClickListener, "formUtils", Mockito.mock(FormUtils.class));

        view.setTag(R.id.specify_context, view.getContext());

        panelRecordButtonClickListener.onClick(view);

        //disableExpansionPanelViews
        Assert.assertFalse(expansionHeaderLayout.isEnabled());
        Assert.assertFalse(expansionHeaderLayout.isClickable());

        Assert.assertFalse(statusImageView.isEnabled());
        Assert.assertFalse(statusImageView.isClickable());

        Assert.assertFalse(topBarTextView.isEnabled());
        Assert.assertFalse(topBarTextView.isClickable());

        Assert.assertFalse(okButton.isEnabled());
        Assert.assertFalse(okButton.isClickable());

        Mockito.verify(panelRecordButtonClickListener, Mockito.times(1)).initiateTask(Mockito.any(View.class));
    }
}