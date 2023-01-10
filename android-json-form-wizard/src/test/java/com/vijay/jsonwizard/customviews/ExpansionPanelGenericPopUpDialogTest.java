package com.vijay.jsonwizard.customviews;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.customviews.ExpansionPanelGenericPopupDialog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

@RunWith(PowerMockRunner.class)
public class ExpansionPanelGenericPopUpDialogTest extends BaseTest {
    private final String theAccordion = "{\n" +
            "    \"title\": \"Expansion Panel\",\n" +
            "    \"fields\": [\n" +
            "      {\n" +
            "        \"key\": \"accordion_panel_demo\",\n" +
            "        \"openmrs_entity_parent\": \"\",\n" +
            "        \"openmrs_entity\": \"\",\n" +
            "        \"openmrs_entity_id\": \"\",\n" +
            "        \"text\": \"Expansion Panel Demo\",\n" +
            "        \"type\": \"expansion_panel\",\n" +
            "        \"content_form\": \"expansion_panel_sub_form\",\n" +
            "        \"container\": \"anc_test\"\n" +
            "      }\n" +
            "    ]\n" +
            "  }";
    @Mock
    private ExpansionPanelGenericPopupDialog expansionPanelGenericPopupDialog;

    @Before
    public void setUp() {
        expansionPanelGenericPopupDialog = Mockito.mock(ExpansionPanelGenericPopupDialog.class);

    }

    @Test
    public void testAddRequiredFields() throws Exception {
        JSONArray jsonArray = new JSONObject(theAccordion).getJSONArray("fields");
        Mockito.doReturn(jsonArray).when(expansionPanelGenericPopupDialog).getSubFormsFields();
        Whitebox.invokeMethod(expansionPanelGenericPopupDialog, "addRequiredFields", new JSONObject(theAccordion));
        Assert.assertEquals(1,expansionPanelGenericPopupDialog.getSubFormsFields().length());

    }
}
