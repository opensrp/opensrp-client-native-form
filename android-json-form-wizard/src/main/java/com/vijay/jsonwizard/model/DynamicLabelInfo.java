package com.vijay.jsonwizard.model;

/**
 * Created by Qazi Abubakar
 */
public class DynamicLabelInfo {
    private final String dynamicLabelTitle;
    private final String dynamicLabelText;
    private final String dynamicLabelImageSrc;

    public DynamicLabelInfo(String dynamicLabelTitle, String dynamicLabelText, String dynamicLabelImageSrc) {
        this.dynamicLabelTitle = dynamicLabelTitle;
        this.dynamicLabelText = dynamicLabelText;
        this.dynamicLabelImageSrc = dynamicLabelImageSrc;
    }

    public String getDynamicLabelTitle() {
        return dynamicLabelTitle;
    }

    public String getDynamicLabelText() {
        return dynamicLabelText;
    }

    public String getDynamicLabelImageSrc() {
        return dynamicLabelImageSrc;
    }
}
