package com.vijay.jsonwizard.interfaces;

import android.support.annotation.NonNull;

import org.smartregister.client.utils.contract.ClientFormDao;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 21-05-2020.
 */
public interface RollbackDialogCallback {

    void onFormSelected(@NonNull ClientFormDao.ClientFormModel selectedForm);

    void onCancelClicked();
}
