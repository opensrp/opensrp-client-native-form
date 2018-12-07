package com.vijay.jsonwizard.domain;

import java.io.Serializable;

public class Form implements Serializable {

    private String name;

    private int homeAsUpIndicator;

    private int actionBarBackground;
    private int navigationBackground;

    private boolean wizard = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHomeAsUpIndicator(int homeAsUpIndicator) {
        this.homeAsUpIndicator = homeAsUpIndicator;
    }

    public int getHomeAsUpIndicator() {
        return homeAsUpIndicator;
    }

    public void setActionBarBackground(int actionBarBackground) {
        this.actionBarBackground = actionBarBackground;
    }

    public int getActionBarBackground() {
        return actionBarBackground;
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
}
