package com.vijay.jsonwizard.widgets;

import android.view.View;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.AppExecutors;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;

import java.util.List;
import java.util.Set;

public class TreeViewFactoryTest extends BaseTest {
    private TreeViewFactory factory;
    private JsonFormActivity jsonFormActivity;
    @Mock
    private JsonFormFragment formFragment;
    @Mock
    private CommonListener listener;

    @Before
    public void setUp() throws JSONException {
        MockitoAnnotations.initMocks(this);
        factory = new TreeViewFactory();
        jsonFormActivity = Robolectric.buildActivity(JsonFormActivity.class, getJsonFormActivityIntent()).create().get();
    }

    @Test
    public void testTreeViewFactoryInstantiatesViewsCorrectly() throws Exception {
        String treeViewFactoryString = "{\"key\":\"Home_Facility\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"openmrs_data_type\":\"text\",\"type\":\"tree\",\"hint\":\"Child's home health facility\",\"tree\":[{\"name\":\"Hilton\",\"key\":\"hilton\",\"level\":\"1\",\"nodes\":[{\"name\":\"Sarova\",\"key\":\"sarova\"}]},{\"name\":\"Double tree\",\"key\":\"double_tree\"}],\"default\":[\"hilton\"],\"value\":[\"sarova\"],\"v_required\":{\"value\":true,\"err\":\"Please enter the child's home facility\"},\"read_only\":true,\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tree_relevance_rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tree_constraints_rules.yml\"}}}}";
        JSONObject treeViewFactoryObject = new JSONObject(treeViewFactoryString);
        buildMockedJsonFormFragment();
        List<View> viewList = factory.getViewsFromJson("RandomStepName", jsonFormActivity, formFragment, treeViewFactoryObject, listener);
        Assert.assertNotNull(viewList);
        Assert.assertEquals(1, viewList.size());

        View view = viewList.get(0);
        RelativeLayout relativeLayout = view.findViewById(R.id.edit_text_layout);

        MaterialEditText materialEditText = (MaterialEditText) relativeLayout.getChildAt(0);
        Assert.assertNotNull(materialEditText);
        Assert.assertEquals("Home_Facility", materialEditText.getTag(R.id.key));
    }

    @Test
    public void testGetCustomTranslatableWidgetFields() {
        Set<String> editableProperties = factory.getCustomTranslatableWidgetFields();
        Assert.assertEquals(0, editableProperties.size());
    }

    private void buildMockedJsonFormFragment() {
        JsonFormActivity jsonFormActivitySpy = Mockito.spy(new JsonFormActivity());
        Mockito.doReturn(new AppExecutors()).when(jsonFormActivitySpy).getAppExecutors();
        Mockito.doReturn(jsonFormActivitySpy).when(formFragment).getJsonApi();
    }
}
