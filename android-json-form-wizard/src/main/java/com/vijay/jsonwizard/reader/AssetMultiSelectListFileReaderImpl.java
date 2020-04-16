package com.vijay.jsonwizard.reader;

import android.content.Context;

import org.apache.commons.lang3.CharEncoding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import timber.log.Timber;
/***
 * @deprecated should now use MultiSelectListRepository
 */
public class AssetMultiSelectListFileReaderImpl implements MultiSelectListFileReader {

    private Context context;
    private String directory;

    public AssetMultiSelectListFileReaderImpl(Context context, String directory) {
        this.context = context;
        this.directory = directory;
    }

    @Override
    public String read(String fileName) {
        InputStream inputStream;
        StringBuilder stringBuilder;
        try {
            String locale = context.getResources().getConfiguration().locale.getLanguage();
            locale = locale.equalsIgnoreCase("en") ? "" : "-" + locale;
            String path = directory + locale + "/" + fileName;
            inputStream = context.getAssets().open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, CharEncoding.UTF_8));
            String line;
            stringBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            inputStream.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            Timber.e(e);
            return null;
        }
    }

}
