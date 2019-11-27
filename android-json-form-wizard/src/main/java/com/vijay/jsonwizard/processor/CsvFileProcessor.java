package com.vijay.jsonwizard.processor;

import androidx.annotation.NonNull;

public interface  CsvFileProcessor {
    Object process(@NonNull String data);
}
