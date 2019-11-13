package com.vijay.jsonwizard.task;

import android.content.Context;
import android.os.AsyncTask;

import com.vijay.jsonwizard.reader.MultiSelectFileReader;
import com.vijay.jsonwizard.utils.MultiSelectListUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import timber.log.Timber;

public class MultiSelectListSaveOptionsTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;
    private MultiSelectFileReader multiSelectFileReader;

    public MultiSelectListSaveOptionsTask(Context context, MultiSelectFileReader multiSelectFileReader) {
        this.context = new WeakReference<>(context);
        this.multiSelectFileReader = multiSelectFileReader;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            MultiSelectListUtils.saveMultiSelectListOptions(context.get(), multiSelectFileReader);
        } catch (IOException e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Timber.i("Files have been Saved");
    }
}
