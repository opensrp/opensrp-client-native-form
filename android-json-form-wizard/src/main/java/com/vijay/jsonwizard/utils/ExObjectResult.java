package com.vijay.jsonwizard.utils;

public class ExObjectResult {
    private boolean isFinal;
    private boolean relevant;

    public ExObjectResult(boolean relevant, boolean aFinal) {
        isFinal = aFinal;
        this.relevant = relevant;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isRelevant() {
        return relevant;
    }
}
