package org.smartregister.nativeform_tester.domain;

import android.content.Context;

import com.vijay.jsonwizard.domain.Form;
import com.vijay.jsonwizard.utils.FileReaderUtil;
import com.vijay.jsonwizard.utils.ResourceType;


import java.util.Set;

public class ConfigForm {

    private String name;

    private Integer homeAsUpIndicator;

    private String actionBarBackground;
    private String navigationBackground;

    private String backIcon;

    private String nextLabel;
    private String previousLabel;
    private String saveLabel;

    private Boolean wizard = true;
    private Boolean hideSaveLabel = false;

    private Boolean hideNextButton = false;
    private Boolean hidePreviousButton = false;

    private Set<String> hiddenFields;

    private Set<String> disabledFields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getHomeAsUpIndicator() {
        return homeAsUpIndicator;
    }

    public void setHomeAsUpIndicator(Integer homeAsUpIndicator) {
        this.homeAsUpIndicator = homeAsUpIndicator;
    }

    public String getActionBarBackground() {
        return actionBarBackground;
    }

    public void setActionBarBackground(String actionBarBackground) {
        this.actionBarBackground = actionBarBackground;
    }

    public String getNavigationBackground() {
        return navigationBackground;
    }

    public void setNavigationBackground(String navigationBackground) {
        this.navigationBackground = navigationBackground;
    }

    public String getBackIcon() {
        return backIcon;
    }

    public void setBackIcon(String backIcon) {
        this.backIcon = backIcon;
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

    public Boolean isWizard() {
        return wizard;
    }

    public void setWizard(Boolean wizard) {
        this.wizard = wizard;
    }

    public Boolean isHideSaveLabel() {
        return hideSaveLabel;
    }

    public void setHideSaveLabel(Boolean hideSaveLabel) {
        this.hideSaveLabel = hideSaveLabel;
    }

    public Boolean isHideNextButton() {
        return hideNextButton;
    }

    public void setHideNextButton(Boolean hideNextButton) {
        this.hideNextButton = hideNextButton;
    }

    public Boolean isHidePreviousButton() {
        return hidePreviousButton;
    }

    public void setHidePreviousButton(Boolean hidePreviousButton) {
        this.hidePreviousButton = hidePreviousButton;
    }

    public Set<String> getHiddenFields() {
        return hiddenFields;
    }

    public void setHiddenFields(Set<String> hiddenFields) {
        this.hiddenFields = hiddenFields;
    }

    public Set<String> getDisabledFields() {
        return disabledFields;
    }

    public void setDisabledFields(Set<String> disabledFields) {
        this.disabledFields = disabledFields;
    }

    public Form toForm(Context context) {
        Form form = new Form();
        if (getName() != null)
            form.setName(getName());

        if (getHomeAsUpIndicator() != null)
            form.setHomeAsUpIndicator(getHomeAsUpIndicator());

        if (getNavigationBackground() != null)
            form.setNavigationBackground(FileReaderUtil.getResourceId(context, getNavigationBackground(), ResourceType.COLOR));

        if (getActionBarBackground() != null)
            form.setActionBarBackground(FileReaderUtil.getResourceId(context, getActionBarBackground(), ResourceType.COLOR));

        if (getBackIcon() != null)
            form.setBackIcon(FileReaderUtil.getResourceId(context, getActionBarBackground(), ResourceType.DRAWABLE));

        if (getNextLabel() != null)
            form.setNextLabel(getNextLabel());

        if (getPreviousLabel() != null)
            form.setNextLabel(getPreviousLabel());

        if (getSaveLabel() != null)
            form.setSaveLabel(getSaveLabel());

        if (isWizard() != null)
            form.setWizard(isWizard());

        if (isHideSaveLabel() != null)
            form.setHideSaveLabel(isHideSaveLabel());

        if (isHideNextButton() != null)
            form.setHideNextButton(isHideNextButton());

        if (isHidePreviousButton() != null)
            form.setHidePreviousButton(isHidePreviousButton());

        if (getHiddenFields() != null)
            form.setHiddenFields(getHiddenFields());

        return form;
    }
}
