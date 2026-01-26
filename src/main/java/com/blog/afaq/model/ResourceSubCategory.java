package com.blog.afaq.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
@Getter
public enum ResourceSubCategory {

    NATIONAL(ResourceCategory.LEGAL, NationalSubCategory.values()),
    INTERNATIONAL(ResourceCategory.LEGAL),
    CASE_LAW(ResourceCategory.DIVERSE),
    OPINIONS(ResourceCategory.DIVERSE),
    OTHER(ResourceCategory.DIVERSE);

    private final ResourceCategory parentCategory;
    private final List<? extends Enum<?>> subSubCategories;

    ResourceSubCategory(ResourceCategory parentCategory) {
        this(parentCategory, null);
    }

    ResourceSubCategory(ResourceCategory parentCategory, Enum<?>[] subSubCategories) {
        this.parentCategory = parentCategory;
        this.subSubCategories = subSubCategories != null ? Arrays.asList(subSubCategories) : null;
    }

    // Nested enum for NATIONAL sub-subcategories
    public enum NationalSubCategory {
        EMIRATES,
        BAHRAIN,
        SAUDI_ARABIA,
        OMAN,
        QATAR,
        KUWAIT;
    }
}
