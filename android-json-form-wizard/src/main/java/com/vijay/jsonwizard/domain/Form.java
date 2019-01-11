package com.vijay.jsonwizard.domain;

import java.io.Serializable;

public class Form implements Serializable {

    private String name;

    private int homeAsUpIndicator;

    private int actionBarBackground;
    private int navigationBackground;

    private String nextLabel;
    private String previousLabel;
    private String saveLabel;

    private boolean wizard = true;
    private boolean hideSaveLabel = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHomeAsUpIndicator() {
        return homeAsUpIndicator;
    }

    public void setHomeAsUpIndicator(int homeAsUpIndicator) {
        this.homeAsUpIndicator = homeAsUpIndicator;
    }

    public int getActionBarBackground() {
        return actionBarBackground;
    }

    public void setActionBarBackground(int actionBarBackground) {
        this.actionBarBackground = actionBarBackground;
    }

    public int getNavigationBackground() {
        return navigationBackground;
    }

    public void setNavigationBackground(int navigationBackground) {
        this.navigationBackground = navigationBackground;
    }

    public boolean isWizard() {
        return wizard;
    }

    public void setWizard(boolean wizard) {
        this.wizard = wizard;
    }

    public String getNextLabel() {
        return nextLabel;
    }

    public void setNextLabel(String nextLabel) {
        this.nextLabel = nextLabel;
    }

    public String getPreviousLabel() {
        return previousLabel;
    }

    public void setPreviousLabel(String previousLabel) {
        this.previousLabel = previousLabel;
    }

    public String getSaveLabel() {
        return saveLabel;
    }

    public void setSaveLabel(String saveLabel) {
        this.saveLabel = saveLabel;
    }

    public boolean isHideSaveLabel() {
        return hideSaveLabel;
    }

    public void setHideSaveLabel(boolean hideSaveLabel) {
        this.hideSaveLabel = hideSaveLabel;
    }
}