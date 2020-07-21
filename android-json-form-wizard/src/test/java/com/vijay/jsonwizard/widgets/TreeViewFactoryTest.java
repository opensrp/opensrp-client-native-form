package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.view.View;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.shadow.ShadowTreeViewDialog;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import java.util.List;
import java.util.Set;

public class TreeViewFactoryTest extends BaseTest {
    private TreeViewFactory factory;
    @Mock
    private JsonFormActivity context;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;
    @Mock
    private RelativeLayout rootLayout;
    @Mock
    private Resources resources;
    @Mock
    private MaterialEditText editText;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new TreeViewFactory();
    }

    @Test
    @Config(shadows = {ShadowTreeViewDialog.class})
    public void testTreeViewFactoryInstantiatesViewsCorrectly() throws Exception {
        String treeViewFactoryString = "{\"key\":\"Home_Facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"hint\":\"Child's home health facility\",\"tree\":[{\"name\":\"Hilton\",\"key\":\"hilton\",\"level\":\"1\",\"nodes\":[{\"name\":\"Sarova\",\"key\":\"sarova\"}]},{\"name\":\"Double tree\",\"key\":\"double_tree\"}],\"default\":\"Hilton\",\"v_required\":{\"value\":false,\"err\":\"Please enter the child's home facility\"},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tree_relevance_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tree_constraints_rules.yml\"}}}}";
        JSONObject treeViewFactoryObject = new JSONObject(treeViewFactoryString);
        Assert.assertNotNull(treeViewFactoryString);
        TreeViewFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRootLayout(context);
        Mockito.doReturn(editText).when(rootLayout).findViewById(R.id.edit_text);
        Mockito.doReturn(resources).when(context).getResources();

        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, treeViewFactoryObject, listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        TreeViewFactory factorySpy = Mockito.spy(factory);

        Set<String> editableProperties = factorySpy.getCustomTranslatableWidgetFields();
        Assert.assertEquals(0, editableProperties.size());
    }
}
