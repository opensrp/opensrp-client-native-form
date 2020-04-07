package org.smartregister.nativeform.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileReaderUtil {

    public static String getStringFromFile(File sourceFile) throws Exception {
        FileInputStream fin = new FileInputStream(sourceFile);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static int getResourceId(Context context, String name, ResourceType resourceType)
    {
        try {
            return context.getResources().getIdentifier(name, resourceType.getType(), context.getPackageName());
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

}
