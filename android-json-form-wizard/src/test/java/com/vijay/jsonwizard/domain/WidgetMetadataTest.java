package com.vijay.jsonwizard.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Vincent Karuri on 21/07/2020
 */
public class WidgetMetadataTest {

    @Test
    public void testWidgetMetadataConstructorShouldCorrectlyUpdateFields() {
        WidgetMetadata widgetMetadata = new WidgetMetadata();
        String openmrsEntity = "openmrs_entity";
        String openmrsEntityId = "openmrs_entity_id";
        String openmrsEntityParent = "openmrs_entity_parent";
        String relevance = "relevance";
        widgetMetadata.withOpenMrsEntity(openmrsEntity)
                .withOpenMrsEntityId(openmrsEntityId)
                .withOpenMrsEntityParent(openmrsEntityParent)
                .withRelevance(relevance);

        assertEquals(openmrsEntity, widgetMetadata.getOpenMrsEntity());
        assertEquals(openmrsEntityId, widgetMetadata.getOpenMrsEntityId());
        assertEquals(openmrsEntityParent, widgetMetadata.getOpenMrsEntityParent());
        assertEquals(relevance, widgetMetadata.getRelevance());
    }
}
