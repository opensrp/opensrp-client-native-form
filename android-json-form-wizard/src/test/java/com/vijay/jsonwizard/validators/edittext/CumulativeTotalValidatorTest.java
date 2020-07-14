package com.vijay.jsonwizard.validators.edittext;

import android.text.Editable;
import android.text.SpannableStringBuilder;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONArray;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by Vincent Karuri on 07/07/2020
 */
public class CumulativeTotalValidatorTest extends BaseTest {

    @Mock
    private JsonApi jsonApi;

    @Mock
    private MaterialEditText currMaterialEditText;

    private JSONArray relatedFields;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        relatedFields = new JSONArray();
        relatedFields.put("key1");
        relatedFields.put("key2");
        relatedFields.put("key3");
    }

    @Test
    public void testIsValidShouldReturnTrueForEmptyEditText() {
        CumulativeTotalValidator validator = new CumulativeTotalValidator("", mock(JsonFormFragment.class), "step1", "key4", relatedFields, jsonApi);
        assertTrue(validator.isValid(currMaterialEditText, true));
    }

    @Test
    public void testIsValidShouldReturnCorrectStatus() {
        mockEditTextTotals("6", "12");
        CumulativeTotalValidator validator = new CumulativeTotalValidator("", mock(JsonFormFragment.class), "step1", "key4", relatedFields, jsonApi);
        assertTrue(validator.isValid(currMaterialEditText, false));
        mockEditTextTotals("6", "10");
        assertFalse(validator.isValid(currMaterialEditText, false));
        mockEditTextTotals("", "6");
        assertTrue(validator.isValid(currMaterialEditText, false));
        mockEditTextTotals("", "7");
        assertFalse(validator.isValid(currMaterialEditText, false));
    }

    private void mockEditTextTotals(String currEditTextVal, String total) {
        MaterialEditText materialEditText1 = mock(MaterialEditText.class);
        doReturn(getEditable("1")).when(materialEditText1).getText();
        doReturn(materialEditText1).when(jsonApi).getFormDataView("step1:key1");
        MaterialEditText materialEditText2 = mock(MaterialEditText.class);
        doReturn(getEditable("2")).when(materialEditText2).getText();
        doReturn(materialEditText2).when(jsonApi).getFormDataView("step1:key2");
        MaterialEditText materialEditText3 = mock(MaterialEditText.class);
        doReturn(getEditable("3")).when(materialEditText3).getText();
        doReturn(materialEditText3).when(jsonApi).getFormDataView("step1:key3");
        MaterialEditText materialEditText4 = mock(MaterialEditText.class);
        doReturn(getEditable(total)).when(materialEditText4).getText();
        doReturn(materialEditText4).when(jsonApi).getFormDataView("step1:key4");
        doReturn(getEditable(currEditTextVal)).when(currMaterialEditText).getText();
    }

    private Editable getEditable(String str) {
        return new SpannableStringBuilder(str);
    }
}
