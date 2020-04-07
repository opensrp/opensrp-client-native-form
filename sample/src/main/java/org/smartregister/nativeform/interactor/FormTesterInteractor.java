package org.smartregister.nativeform.interactor;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.smartregister.nativeform.contract.FormTesterContract;
import org.smartregister.nativeform.domain.ConfigForm;
import org.smartregister.nativeform.domain.JsonForm;
import org.smartregister.nativeform.util.FileReaderUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormTesterInteractor implements FormTesterContract.Interactor {
    private Gson gson = null;

    @Override
    public void exportDefaultForms(Context context, FormTesterContract.Presenter presenter) {
        AssetManager assetManager = context.getAssets();
        try {
            String root = verifyOrCreateDiskDirectory();

            String[] image_source = {"json.form", "json.form.config", "img", "image", "rule"};
            for (String sourceDir : image_source) {
                exportDirectory(assetManager, sourceDir, context, root, true);
            }

            readForms(context, presenter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exportDirectory(AssetManager assetManager, String sourceDir, Context context, String root, boolean createRoot) throws IOException {
        String[] local_assets = assetManager.list(sourceDir);
        if (local_assets != null && local_assets.length > 0) {
            for (String assetName : local_assets) {
                // export all in
                try {

                    // if the assetName is a folder extract this
                    String[] child_assets = assetManager.list(sourceDir + "/" + assetName);
                    if (child_assets != null && child_assets.length > 0) {
                        String destinationRoot = root + "/" + assetName;
                        File myDir = new File(destinationRoot);
                        if (!myDir.exists())
                            myDir.mkdirs();

                        exportDirectory(assetManager, sourceDir + "/" + assetName, context, destinationRoot, false);

                    } else {
                        String fileRoot = createRoot ? root + "/" + sourceDir : root;
                        if (createRoot) {
                            File myDir = new File(root + "/" + sourceDir);
                            if (!myDir.exists())
                                myDir.mkdirs();
                        }

                        exportBinaryAsset(assetName, context, fileRoot, sourceDir);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void exportBinaryAsset(String assetName, Context context, String root, String rootSource) throws IOException {
        InputStream inputStream = context.getAssets()
                .open(rootSource + "/" + assetName);

        byte[] targetArray = new byte[inputStream.available()];
        inputStream.read(targetArray);

        File file = new File(root, assetName);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(targetArray);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void validateForm(Context context, String formName) {

    }

    @Override
    public String verifyOrCreateDiskDirectory() {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File myDir = new File(root + "/" + JsonFormConstants.DEFAULT_FORMS_DIRECTORY);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        return myDir.toString();
    }

    @Override
    public boolean verifyFormsDirectoryExists() {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File myDir = new File(root + "/" + JsonFormConstants.DEFAULT_FORMS_DIRECTORY + "/json.form/");
        return myDir.exists();
    }

    @Override
    public List<File> readForms(Context context, FormTesterContract.Presenter presenter) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File myDir = new File(root + "/" + JsonFormConstants.DEFAULT_FORMS_DIRECTORY + "/json.form/");

        List<File> files = new ArrayList<>();
        if (myDir.exists()) {
            List<FormTesterContract.NativeForm> nativeForms = new ArrayList<>();
            for (File file : myDir.listFiles()) {
                String type = getFileExtension(file.getName());
                if (type.equalsIgnoreCase("json"))
                    nativeForms.add(new JsonForm(file, readFileForm(context, file.getName())));
            }

            Collections.sort(nativeForms, (o1, o2) -> o1.getFileName().compareTo(o2.getFileName()));
            presenter.onFormsRead(nativeForms);
        }

        return files;
    }

    @Nullable
    private Form readFileForm(Context context, String formName) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File formFile = new File(root + "/" + JsonFormConstants.DEFAULT_FORMS_DIRECTORY + "/json.form.config/" + formName);
        if (formFile.exists()) {
            // parse and create a form object
            if (gson == null)
                gson = new Gson();

            try {
                String content = FileReaderUtil.getStringFromFile(formFile);
                ConfigForm configForm = gson.fromJson(content, ConfigForm.class);
                return configForm.toForm(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private String getFileExtension(String fullName) {
        String fileName = new File(fullName).getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }


    private String getStringFromFile(File sourceFile) throws Exception {
        FileInputStream fin = new FileInputStream(sourceFile);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    private String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
