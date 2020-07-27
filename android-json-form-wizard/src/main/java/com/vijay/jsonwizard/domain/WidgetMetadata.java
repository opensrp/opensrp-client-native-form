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

    public void setOpenMrsEntityParent(String openMrsEntityParent) {
        this.openMrsEntityParent = openMrsEntityParent;
    }

    public void setOpenMrsEntity(String openMrsEntity) {
        this.openMrsEntity = openMrsEntity;
    }

    public void setOpenMrsEntityId(String openMrsEntityId) {
        this.openMrsEntityId = openMrsEntityId;
    }

    public void setRelevance(String relevance) {
        this.relevance = relevance;
    }

    public WidgetMetadata withOpenMrsEntityParent(String openMrsEntityParent) {
        setOpenMrsEntityParent(openMrsEntityParent);
        return this;
    }

    public WidgetMetadata withOpenMrsEntity(String openMrsEntity) {
        setOpenMrsEntity(openMrsEntity);
        return this;
    }

    public WidgetMetadata withOpenMrsEntityId(String openMrsEntityId) {
        setOpenMrsEntityId(openMrsEntityId);
        return this;
    }

    public WidgetMetadata withRelevance(String relevance) {
        setRelevance(relevance);
        return this;
    }
}
