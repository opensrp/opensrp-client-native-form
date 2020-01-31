package com.vijay.jsonwizard.interfaces;

import com.vijay.jsonwizard.domain.MultiSelectItem;

import java.util.List;

public interface MultiSelectListRepository {
    List<MultiSelectItem> fetchData();
}
