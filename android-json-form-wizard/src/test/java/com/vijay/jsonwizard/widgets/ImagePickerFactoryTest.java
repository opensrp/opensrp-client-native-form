package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

public class ImagePickerFactoryTest extends BaseTest {
    private ImagePickerFactory factory;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private DisplayMetrics displayMetrics;

    @Mock
    private ImageView imageView;

    @Mock
    private Button button;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new ImagePickerFactory();
    }

    @Test
    public void testImagePickerFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        ImagePickerFactory factorySpy = Mockito.spy(factory);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(imageView).when(factorySpy).getImageView(context);
        Mockito.doReturn(button).when(factorySpy).getButton(context);
        Mockito.doReturn(displayMetrics).when(resources).getDisplayMetrics();

        String imagePickerWidget = "{\"key\":\"user_image\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"choose_image\",\"uploadButtonText\":\"Take a photo of the child\",\"read_only\":true,\"relevance\":{\"step1:user_first_name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"test\\\")\"}},\"v_required\":{\"value\":true,\"err\":\"this field is required\"},\"value\":\"/data/pathtoimage/image.jpg\"}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(imagePickerWidget), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(2, viewList.size());
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Assert.assertNotNull(factory);
        ImagePickerFactory factorySpy = Mockito.spy(factory);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(1, editableProperties.size());
        Assert.assertEquals("uploadButtonText", editableProperties.iterator().next());
    }

}
