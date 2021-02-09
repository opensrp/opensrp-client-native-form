package com.vijay.jsonwizard.utils;


import android.app.AlertDialog;
import android.content.DialogInterface;

import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.RollbackDialogCallback;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowAlertDialog;
import org.smartregister.client.utils.contract.ClientFormContract;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 29-06-2020.
 */
public class FormRollbackDialogUtilTest extends BaseTest {

    @Test
    public void selectFormShouldReturnFalseWhenCurrentCorruptedFormIsChosen() {
        ClientFormContract.Model clientForm = new TestClientForm();

        assertFalse(FormRollbackDialogUtil.selectForm(Mockito.mock(ClientFormContract.Dao.class), 0, "0.0.3" + RuntimeEnvironment.application.getString(R.string.current_corrupted_form)
                , RuntimeEnvironment.application, new ArrayList<ClientFormContract.Model>(), clientForm, Mockito.mock(RollbackDialogCallback.class)));
    }

    @Test
    public void selectFormShouldReturnFalseWhenItemIndexDoesNotExist() {
        ClientFormContract.Model clientForm = new TestClientForm();

        assertFalse(FormRollbackDialogUtil.selectForm(Mockito.mock(ClientFormContract.Dao.class), 2, "0.0.3"
                , RuntimeEnvironment.application, new ArrayList<ClientFormContract.Model>(), clientForm, Mockito.mock(RollbackDialogCallback.class)));
    }

    @Test
    public void selectFormShouldReturnTrueWhenAConfigurableFormIsSelected() {
        ClientFormContract.Model highClientFormVersion = new TestClientForm();
        highClientFormVersion.setVersion("0.0.3");

        ClientFormContract.Model clientForm = new TestClientForm();
        clientForm.setVersion("0.0.2");
        ArrayList<ClientFormContract.Model> clientFormsList = new ArrayList<ClientFormContract.Model>();
        clientFormsList.add(clientForm);

        ClientFormContract.Dao clientFormRepository = Mockito.mock(ClientFormContract.Dao.class);

        RollbackDialogCallback rollbackDialogCallback = Mockito.mock(RollbackDialogCallback.class);
        assertTrue(FormRollbackDialogUtil.selectForm(clientFormRepository, 0, "0.0.2"
                , RuntimeEnvironment.application, clientFormsList, highClientFormVersion, rollbackDialogCallback));
        Mockito.verify(rollbackDialogCallback).onFormSelected(clientForm);


        ArgumentCaptor<ClientFormContract.Model> updateClientFormArgumentCaptor = ArgumentCaptor.forClass(ClientFormContract.Model.class);
        Mockito.verify(clientFormRepository, Mockito.times(2)).addOrUpdate(updateClientFormArgumentCaptor.capture());
        ClientFormContract.Model updatedClientForm1 = updateClientFormArgumentCaptor.getAllValues().get(0);
        assertEquals("0.0.2", updatedClientForm1.getVersion());
        assertTrue(updatedClientForm1.isActive());

        ClientFormContract.Model updatedClientForm2 = updateClientFormArgumentCaptor.getAllValues().get(1);
        assertEquals("0.0.3", updatedClientForm2.getVersion());
        assertFalse(updatedClientForm2.isActive());


        ArgumentCaptor<ClientFormContract.Model> clientFormArgumentCaptor = ArgumentCaptor.forClass(ClientFormContract.Model.class);
        Mockito.verify(rollbackDialogCallback).onFormSelected(clientFormArgumentCaptor.capture());
        ClientFormContract.Model selectedClientForm = clientFormArgumentCaptor.getValue();
        assertEquals("0.0.2", selectedClientForm.getVersion());
    }


    @Test
    public void selectFormShouldReturnTrueWhenBaseFormIsSelected() {
        ClientFormContract.Model highClientFormVersion = new TestClientForm();
        highClientFormVersion.setVersion("0.0.3");

        ClientFormContract.Model clientForm = new TestClientForm();
        clientForm.setVersion("0.0.2");
        ArrayList<ClientFormContract.Model> clientFormsList = new ArrayList<ClientFormContract.Model>();
        clientFormsList.add(highClientFormVersion);
        clientFormsList.add(clientForm);

        ClientFormContract.Dao clientFormRepository = Mockito.mock(ClientFormContract.Dao.class);
        Mockito.doReturn(new TestClientForm()).when(clientFormRepository).createNewClientFormModel();

        RollbackDialogCallback rollbackDialogCallback = Mockito.mock(RollbackDialogCallback.class);
        assertTrue(FormRollbackDialogUtil.selectForm(clientFormRepository, 0, "base version"
                , RuntimeEnvironment.application, clientFormsList, highClientFormVersion, rollbackDialogCallback));

        ArgumentCaptor<ClientFormContract.Model> clientFormArgumentCaptor = ArgumentCaptor.forClass(ClientFormContract.Model.class);
        Mockito.verify(rollbackDialogCallback).onFormSelected(clientFormArgumentCaptor.capture());
        ClientFormContract.Model selectedClientForm = clientFormArgumentCaptor.getValue();
        assertEquals(JsonFormConstants.CLIENT_FORM_ASSET_VERSION, selectedClientForm.getVersion());
    }

    @Test
    public void testShowAvailableRollbackFormsDialogShowAlertDialog() {
        ClientFormContract.Dao mockClientFormRepository = Mockito.mock(ClientFormContract.Dao.class);
        RollbackDialogCallback mockRollbackDialogCallback = Mockito.mock(RollbackDialogCallback.class);
        ClientFormContract.Model model = new TestClientForm();
        model.setActive(true);
        model.setVersion("v0.0.1");

        JsonFormActivity activity = Mockito.spy(Robolectric.buildActivity(JsonFormActivity.class).get());

        AlertDialog alertDialog = FormRollbackDialogUtil
                .showAvailableRollbackFormsDialog(activity, mockClientFormRepository,
                        Collections.singletonList(model), model, mockRollbackDialogCallback);


        Mockito.verify(activity).setVisibleFormErrorAndRollbackDialog(ArgumentMatchers.eq(true));

        assertNotNull(ShadowAlertDialog.getLatestAlertDialog());

        //On dismiss should also invoke expected methods
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();

        Mockito.verify(activity).setVisibleFormErrorAndRollbackDialog(ArgumentMatchers.eq(false));

        Mockito.verify(mockRollbackDialogCallback).onCancelClicked();

        activity.finish();
    }
}