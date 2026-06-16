package com.om.sitaramfrem.listners;

import com.om.sitaramfrem.models.view_all_category.ViewAllCategoryModel;

public interface CategoryClickListener {
    void onCategoryClick(ViewAllCategoryModel.Data.CategoryImage itemData);

    void onAddToCartClick(int position);

    void onDownloadClick(String imgUrl);
}
