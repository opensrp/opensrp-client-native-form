package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Set;

public class ImageViewFactoryTest extends BaseTest {
    private ImageViewFactory factory;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private View rootLayout;

    @Mock
    private TextView descriptionTextView;

    @Mock
    private Bitmap bitmap;

    @Mock
    private ImageView imageView;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new ImageViewFactory();
    }

    @Test
    public void testImagePickerFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        ImageViewFactory factorySpy = Mockito.spy(factory);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRootLayout(context);
        Mockito.doReturn(descriptionTextView).when(rootLayout).findViewById(R.id.imageViewLabel);
        Mockito.doReturn(bitmap).when(factorySpy).getBitmap(ArgumentMatchers.eq(context), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(imageView).when(rootLayout).findViewById(R.id.image);

        String imagePickerWidget = "{\"key\":\"illustration_text_description\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"image_view\",\"text\":\"Take the test by doing it as shown in the image below\",\"label_text_size\":\"18sp\",\"image_file\":\"process.jpeg\",\"text_color\":\"#000000\"}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(imagePickerWidget), listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Assert.assertNotNull(factory);
        ImageViewFactory factorySpy = Mockito.spy(factory);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(0, editableProperties.size());
    }
}
