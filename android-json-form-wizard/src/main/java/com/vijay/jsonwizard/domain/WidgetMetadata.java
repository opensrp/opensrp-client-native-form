package com.vijay.jsonwizard.domain;

/**
 * Created by Vincent Karuri on 19/08/2019
 */
public class WidgetMetadata {

    private String openMrsEntityParent;
    private String openMrsEntity;
    private String openMrsEntityId;
    private String relevance;

    public String getOpenMrsEntityParent() {
        return openMrsEntityParent;
    }

    public String getOpenMrsEntity() {
        return openMrsEntity;
    }

    public String getOpenMrsEntityId() {
        return openMrsEntityId;
    }

    public String getRelevance() {
        return relevance;
    }

    public WidgetMetadata setOpenMrsEntityParent(String openMrsEntityParent) {
        this.openMrsEntityParent = openMrsEntityParent;
        return this;
    }

    public WidgetMetadata setOpenMrsEntity(String openMrsEntity) {
        this.openMrsEntity = openMrsEntity;
        return this;
    }

    public WidgetMetadata setOpenMrsEntityId(String openMrsEntityId) {
        this.openMrsEntityId = openMrsEntityId;
        return this;
    }

    public WidgetMetadata setRelevance(String relevance) {
        this.relevance = relevance;
        return this;
    }

    public WidgetMetadata withOpenMrsEntityParent(String openMrsEntityParent) {
        this.openMrsEntityParent = openMrsEntityParent;
        return this;
    }

    public WidgetMetadata withOpenMrsEntity(String openMrsEntity) {
        this.openMrsEntity = openMrsEntity;
        return this;
    }

    public WidgetMetadata withOpenMrsEntityId(String openMrsEntityId) {
        this.openMrsEntityId = openMrsEntityId;
        return this;
    }

    public WidgetMetadata withRelevance(String relevance) {
        this.relevance = relevance;
        return this;
    }
}
