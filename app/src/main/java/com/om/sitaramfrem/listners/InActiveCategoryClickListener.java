package com.om.sitaramfrem.listners;

import com.om.sitaramfrem.models.inactive_category.InActiveCategoryModel;
import com.om.sitaramfrem.models.view_all_category.ViewAllCategoryModel;

public interface InActiveCategoryClickListener {
    void onCategoryClick(InActiveCategoryModel.Data itemData);
}
