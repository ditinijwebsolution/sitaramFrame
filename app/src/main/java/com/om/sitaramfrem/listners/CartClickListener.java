package com.om.sitaramfrem.listners;

import com.om.sitaramfrem.models.view_all_category.ViewAllCategoryModel;

public interface CartClickListener {
    void onRemoveCartClick(int position);
    void onEditCartClick(int position, String flag, int qty);
}
