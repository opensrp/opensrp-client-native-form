package com.vijay.jsonwizard.utils;

public enum ResourceType {
    DRAWABLE("drawable") , STRING("string"), COLOR("color") ;

    private String type;
    ResourceType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
