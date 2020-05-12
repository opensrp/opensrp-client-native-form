package com.vijay.jsonwizard.widgets;

import android.content.res.Resources;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

public class EditTextFactoryTest extends BaseTest {
    private EditTextFactory factory;
    @Mock
    private JsonFormActivity context;

    @Mock
    private JsonFormFragment formFragment;

    @Mock
    private Resources resources;

    @Mock
    private CommonListener listener;

    @Mock
    private RelativeLayout rootLayout;

    @Mock
    private RelativeLayout editTextLayout;

    @Mock
    private MaterialEditText editText;

    @Mock
    private ImageView editButton;

    private String jsonForm = "{\"encounter_type\":\"Birth Registration\",\"show_errors_on_submit\":true,\"count\":\"1\",\"display_scroll_bars\":true,\"mother\":{\"encounter_type\":\"New Woman Registration\"},\"entity_id\":\"\",\"relational_id\":\"\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"step1\":{\"display_back_button\":\"true\",\"title\":\"Birth Registration\",\"fields\":[{\"key\":\"medications_other\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"Specify\",\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter valid content\"},\"relevance\":{\"step1:medications\":{\"ex-checkbox\":[{\"or\":[\"other\"]}]}}},{\"key\":\"danger_sign\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"native_radio\",\"label\":\"Does the woman have any of the danger signs in the image?\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"label_info_text\":\"A maximum of up to 3 weekly doses may be required.\",\"label_info_title\":\"Syphilis Compliance\",\"label_info_image_src\":\"img/chw.png\",\"label_info_has_image\":true,\"options\":[{\"key\":\"1\",\"text\":\"Yes\"},{\"key\":\"0\",\"text\":\"No\"}]},{\"key\":\"gps\",\"openmrs_entity_parent\":\"usual_residence\",\"openmrs_entity\":\"person_address\",\"openmrs_entity_id\":\"geopoint\",\"openmrs_data_type\":\"text\",\"type\":\"gps\"}]}}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        factory = new EditTextFactory();
    }

    @Test
    public void testNumberEditTextFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        EditTextFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        FormUtils formUtils = new FormUtils();
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRelativeLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(editTextLayout).when(rootLayout).findViewById(R.id.edit_text_layout);
        Assert.assertNotNull(editTextLayout);

        Mockito.doReturn(editText).when(editTextLayout).findViewById(R.id.edit_text);
        Assert.assertNotNull(editText);

        Mockito.doReturn(editButton).when(editTextLayout).findViewById(R.id.material_edit_text_edit_button);
        Assert.assertNotNull(editButton);

        Editable editable = new SpannableStringBuilder("34");
        Mockito.doReturn(editable).when(editText).getText();

        String gpsString = "{\"key\":\"current_weight\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"5089\",\"type\":\"edit_text\",\"edit_text_style\":\"bordered\",\"edit_type\":\"number\",\"hint\":\"30\",\"value\":\"34\",\"label_info_text\":\"The into text\",\"label_into_title\":\"The info title\",\"v_numeric\":{\"value\":\"true\",\"err\":\"\"},\"v_numeric_integer\":{\"value\":\"true\",\"err\":\"\"},\"v_min\":{\"value\":\"30\",\"err\":\"Weight must be equal or greater than 30\"},\"v_max\":{\"value\":\"180\",\"err\":\"Weight must be equal or less than 180\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the current weight\"},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-calculation-rules.yml\"}}},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-relevance-rules.yml\"}}},\"constraints\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"sample-constraints-rules.yml\"}}}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(gpsString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
    }

    @Test
    public void testNameEditTextFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        EditTextFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        FormUtils formUtils = new FormUtils();
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRelativeLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(editTextLayout).when(rootLayout).findViewById(R.id.edit_text_layout);
        Assert.assertNotNull(editTextLayout);

        Mockito.doReturn(editText).when(editTextLayout).findViewById(R.id.edit_text);
        Assert.assertNotNull(editText);

        Mockito.doReturn(editButton).when(editTextLayout).findViewById(R.id.material_edit_text_edit_button);
        Assert.assertNotNull(editButton);

        Editable editable = new SpannableStringBuilder("Lifted");
        Mockito.doReturn(editable).when(editText).getText();

        String gpsString = "{\"key\":\"user_last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"User Last name\",\"edit_type\":\"name\",\"value\":\"Lifted\",\"v_min_length\":{\"value\":\"6\",\"err\":\"Please enter a valid name\"},\"v_max_length\":{\"value\":\"10\",\"is_fixed_size\":\"true\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the last name\"},\"v_regex\":{\"value\":\"[A-Za-z\\\\s\\\\.\\\\-]*\",\"err\":\"Please enter a valid name\"},\"v_url\":{\"value\":\"true\",\"err\":\"Please enter a valid name\"},\"v_email\":{\"value\":\"true\",\"err\":\"Please enter a valid name\"},\"relevance\":{\"step1:user_first_name\":{\"type\":\"string\",\"ex\":\"equalTo(., \\\"test\\\")\"}}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(gpsString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
    }

    @Test
    public void testNameWithRelativeNumericIntegerEditTextFactoryInstantiatesViewsCorrectly() throws Exception {
        Assert.assertNotNull(factory);
        EditTextFactory factorySpy = Mockito.spy(factory);
        Assert.assertNotNull(factorySpy);

        FormUtils formUtils = new FormUtils();
        FormUtils formUtilsSpy = Mockito.spy(formUtils);
        Assert.assertNotNull(formUtilsSpy);

        Mockito.doReturn(resources).when(context).getResources();
        Assert.assertNotNull(resources);

        context.setTheme(R.style.NativeFormsAppTheme);
        Mockito.doReturn(rootLayout).when(factorySpy).getRelativeLayout(context);
        Assert.assertNotNull(rootLayout);

        Mockito.doReturn(editTextLayout).when(rootLayout).findViewById(R.id.edit_text_layout);
        Assert.assertNotNull(editTextLayout);

        Mockito.doReturn(editText).when(editTextLayout).findViewById(R.id.edit_text);
        Assert.assertNotNull(editText);

        Mockito.doReturn(editButton).when(editTextLayout).findViewById(R.id.material_edit_text_edit_button);
        Assert.assertNotNull(editButton);

        Editable editable = new SpannableStringBuilder("Lifted");
        Mockito.doReturn(editable).when(editText).getText();

        Mockito.doReturn(jsonForm).when(formFragment).getCurrentJsonState();

        String gpsString = "{\"key\":\"user_last_name\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"edit_text\",\"hint\":\"User Last name\",\"edit_type\":\"name\",\"value\":\"Lifted\",\"v_min_length\":{\"value\":\"6\",\"err\":\"Please enter a valid name\"},\"v_max_length\":{\"value\":\"10\",\"is_fixed_size\":\"true\",\"err\":\"Please enter a valid name\"},\"v_required\":{\"value\":\"true\",\"err\":\"Please enter the last name\"},\"v_relative_max\":{\"value\":\"gps\",\"err\":\"Enter required fields\"}}";
        List<View> viewList = factorySpy.getViewsFromJson("RandomStepName", context, formFragment, new JSONObject(gpsString), listener);
        Assert.assertNotNull(viewList);
        Assert.assertTrue(viewList.size() > 0);
    }
}
