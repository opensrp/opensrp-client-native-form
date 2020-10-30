package com.vijay.jsonwizard.shadow;

import com.vijay.jsonwizard.event.BaseEvent;
import com.vijay.jsonwizard.utils.Utils;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadow.api.Shadow;

/**
 * Created by Vincent Karuri on 08/09/2020
 */

@Implements(Utils.class)
public class ShadowUtils extends Shadow {

    @Implementation
    public static void postEvent(BaseEvent event) {
        // do nothing
    }
}
