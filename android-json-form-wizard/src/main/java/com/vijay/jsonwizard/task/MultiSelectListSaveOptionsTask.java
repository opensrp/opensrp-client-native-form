package com.vijay.jsonwizard.task;

import android.content.Context;
import android.os.AsyncTask;

import com.vijay.jsonwizard.reader.MultiSelectListFileReaderAndProcessor;
import com.vijay.jsonwizard.utils.MultiSelectListUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;

import timber.log.Timber;

public class MultiSelectListSaveOptionsTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Context> context;
    private MultiSelectListFileReaderAndProcessor multiSelectListFileReaderAndProcessor;

    public MultiSelectListSaveOptionsTask(Context context, MultiSelectListFileReaderAndProcessor multiSelectListFileReaderAndProcessor) {
        this.context = new WeakReference<>(context);
        this.multiSelectListFileReaderAndProcessor = multiSelectListFileReaderAndProcessor;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            MultiSelectListUtils.saveMultiSelectListOptions(context.get(), multiSelectListFileReaderAndProcessor);
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
