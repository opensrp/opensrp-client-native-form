package com.vijay.jsonwizard.utils;

public class ExObjectResult {

    public ExObjectResult(boolean relevant, boolean aFinal) {
        isFinal = aFinal;
        this.relevant = relevant;

    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal() {
    }

    public boolean isRelevant() {
        return relevant;
    }

    public void setResult() {
    }

    private boolean isFinal;
    private boolean relevant;
}
