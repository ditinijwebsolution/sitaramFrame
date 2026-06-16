package com.om.sitaramfrem.listners;

import com.om.sitaramfrem.models.view_all_category.ViewAllCategoryModel;

public interface HomeClickListener {
    void onAddToCartClick(int mainPosition, int position);
    void onDownloadClick(String imgUrl);
}
