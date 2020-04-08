package com.vijay.jsonwizard.processor;

import android.support.annotation.NonNull;

/***
 * @deprecated should now use MultiSelectListRepository
 */
public interface  CsvFileProcessor {
    Object process(@NonNull String data);
}
