package com.vijay.jsonwizard.reader;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class MultiSelectFileReader {

    protected String fileName;
    private FileReader fileReader;

    public MultiSelectFileReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public void initMultiSelectFileReader(String fileName) {
        if (isAlreadySaved(fileName)) {
            return;
        }
        this.fileName = fileName;
        String content = read(fileName);
        Object processedContent = process(content);
        save(processedContent);
    }

    private String read(String fileName) {
        return fileReader.read(fileName);
    }

    public JSONArray process(String content) {
        try {
            if (!StringUtils.isBlank(content)) {
                JSONArray jsonArray = new JSONArray();
                String[] lines = content.split("\n");
                String[] headers = lines[0].split(",");
                String[] property = new String[headers.length - 2];
                for (int j = 0; j < property.length; j++) {
                    property[j] = headers[j + 2].replace("property::", "");
                }

                for (int i = 1; i < lines.length; i++) {
                    String[] segments = lines[i].split(",");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(JsonFormConstants.KEY, segments[0]);
                    jsonObject.put(JsonFormConstants.MultiSelectUtils.IS_HEADER, false);
                    jsonObject.put(JsonFormConstants.MultiSelectUtils.TEXT, segments[1]);

                    JSONObject jsonPropertyObject = new JSONObject();
                    for (int k = 0; k < property.length; k++) {
                        if ((k + 3) > segments.length) {
                            jsonPropertyObject.put(property[k], " ");
                        } else {
                            jsonPropertyObject.put(property[k], segments[k + 2]);
                        }
                    }

                    jsonObject.put(JsonFormConstants.MultiSelectUtils.PROPERTY, jsonPropertyObject);
                    jsonArray.put(jsonObject);
                }
                return jsonArray;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected abstract void save(Object processedContent);

    protected abstract boolean isAlreadySaved(String fileName);
}
