package com.vijay.jsonwizard.repository;

import com.vijay.jsonwizard.domain.MultiSelectItem;
import com.vijay.jsonwizard.interfaces.MultiSelectListRepository;

import java.util.Collections;
import java.util.List;

public class TestMultiSelectListRepository implements MultiSelectListRepository {

    @Override
    public List<MultiSelectItem> fetchData() {
        MultiSelectItem multiSelectItem = new MultiSelectItem();
        multiSelectItem.setValue("{}");
        multiSelectItem.setKey("key");
        multiSelectItem.setText("text");
        return Collections.singletonList(multiSelectItem);
    }
}
