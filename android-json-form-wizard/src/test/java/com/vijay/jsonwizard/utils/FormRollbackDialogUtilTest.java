package com.vijay.jsonwizard.utils;


import com.vijay.jsonwizard.BaseTest;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.interfaces.RollbackDialogCallback;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.client.utils.contract.ClientFormDao;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 29-06-2020.
 */
public class FormRollbackDialogUtilTest extends BaseTest {

    @Test
    public void selectFormShouldReturnFalseWhenCurrentCorruptedFormIsChosen() {
        ClientFormDao.ClientFormModel clientForm = new TestClientForm();

        assertFalse(FormRollbackDialogUtil.selectForm(0, "0.0.3" + RuntimeEnvironment.application.getString(R.string.current_corrupted_form)
                , RuntimeEnvironment.application, new ArrayList<ClientForm>(), clientForm, Mockito.mock(RollbackDialogCallback.class)));
    }

    @Test
    public void selectFormShouldReturnFalseWhenItemIndexDoesNotExist() {
        ClientForm clientForm = new ClientForm();

        assertFalse(FormRollbackDialogUtil.selectForm(2, "0.0.3"
                , RuntimeEnvironment.application, new ArrayList<ClientForm>(), clientForm, Mockito.mock(RollbackDialogCallback.class)));
    }

    @Test
    public void selectFormShouldReturnTrueWhenAConfigurableFormIsSelected() {
        ClientForm highClientFormVersion = new ClientForm();
        highClientFormVersion.setVersion("0.0.3");

        ClientForm clientForm = new ClientForm();
        clientForm.setVersion("0.0.2");
        ArrayList<ClientForm> clientFormsList = new ArrayList<ClientForm>();
        clientFormsList.add(clientForm);

        ClientFormRepository clientFormRepository = Mockito.mock(ClientFormRepository.class);
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "clientFormRepository", clientFormRepository);

        RollbackDialogCallback rollbackDialogCallback = Mockito.mock(RollbackDialogCallback.class);
        assertTrue(FormRollbackDialogUtil.selectForm(0, "0.0.2"
                , RuntimeEnvironment.application, clientFormsList, highClientFormVersion, rollbackDialogCallback));
        Mockito.verify(rollbackDialogCallback).onFormSelected(clientForm);


        ArgumentCaptor<ClientForm> updateClientFormArgumentCaptor = ArgumentCaptor.forClass(ClientForm.class);
        Mockito.verify(clientFormRepository, Mockito.times(2)).addOrUpdate(updateClientFormArgumentCaptor.capture());
        ClientForm updatedClientForm1 = updateClientFormArgumentCaptor.getAllValues().get(0);
        assertEquals("0.0.2", updatedClientForm1.getVersion());
        assertTrue(updatedClientForm1.isActive());

        ClientForm updatedClientForm2 = updateClientFormArgumentCaptor.getAllValues().get(1);
        assertEquals("0.0.3", updatedClientForm2.getVersion());
        assertFalse(updatedClientForm2.isActive());


        ArgumentCaptor<ClientForm> clientFormArgumentCaptor = ArgumentCaptor.forClass(ClientForm.class);
        Mockito.verify(rollbackDialogCallback).onFormSelected(clientFormArgumentCaptor.capture());
        ClientForm selectedClientForm = clientFormArgumentCaptor.getValue();
        assertEquals("0.0.2", selectedClientForm.getVersion());
    }


    @Test
    public void selectFormShouldReturnTrueWhenBaseFormIsSelected() {
        ClientForm highClientFormVersion = new ClientForm();
        highClientFormVersion.setVersion("0.0.3");

        ClientForm clientForm = new ClientForm();
        clientForm.setVersion("0.0.2");
        ArrayList<ClientForm> clientFormsList = new ArrayList<ClientForm>();
        clientFormsList.add(highClientFormVersion);
        clientFormsList.add(clientForm);

        ClientFormRepository clientFormRepository = Mockito.mock(ClientFormRepository.class);
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "clientFormRepository", clientFormRepository);

        RollbackDialogCallback rollbackDialogCallback = Mockito.mock(RollbackDialogCallback.class);
        assertTrue(FormRollbackDialogUtil.selectForm(0, "base version"
                , RuntimeEnvironment.application, clientFormsList, highClientFormVersion, rollbackDialogCallback));

        ArgumentCaptor<ClientFormDao.ClientFormModel> clientFormArgumentCaptor = ArgumentCaptor.forClass(ClientForm.class);
        Mockito.verify(rollbackDialogCallback).onFormSelected(clientFormArgumentCaptor.capture());
        ClientFormDao.ClientFormModel selectedClientForm = clientFormArgumentCaptor.getValue();
        assertEquals(JsonFormConstants.CLIENT_FORM_ASSET_VERSION, selectedClientForm.getVersion());
    }
    
    class TestClientForm implements ClientFormDao.ClientFormModel {
        private int id;

        private String version;

        private String identifier;

        private String module;

        private String json;

        private String jurisdiction;

        private String label;

        private boolean active;

        private boolean isNew;

        private Date createdAt;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getIdentifier() {
            return identifier;
        }

        public void setIdentifier(String identifier) {
            this.identifier = identifier;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }

        public String getJurisdiction() {
            return jurisdiction;
        }

        public void setJurisdiction(String jurisdiction) {
            this.jurisdiction = jurisdiction;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public boolean isNew() {
            return isNew;
        }

        public void setNew(boolean aNew) {
            isNew = aNew;
        }

        public Date getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }
    }
}