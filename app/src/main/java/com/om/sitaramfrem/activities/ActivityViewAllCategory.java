package com.om.sitaramfrem.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.om.sitaramfrem.adapters.AdapterViewAllCategory;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.ActivityViewAllCategoryBinding;
import com.om.sitaramfrem.listners.CategoryClickListener;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.models.cart_list.CartListModel;
import com.om.sitaramfrem.models.view_all_category.ViewAllCategoryModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;
import com.om.superrecyclerview.OnMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActivityViewAllCategory extends BaseActivity implements CategoryClickListener {

    private ActivityViewAllCategoryBinding binding;
    private Context mContext;
    private static final String TAG = ActivityViewAllCategory.class.getSimpleName();

    private SessionUtil mSessionUtil;
    private ApiCall mApiCall;

    private int limit = 10;
    private int offset = 0;

    private int id = 0;

    private List<CartListModel.Data> cartList = new ArrayList<>();

    private List<ViewAllCategoryModel.Data.CategoryImage> categoryList = new ArrayList<>();
    AdapterViewAllCategory mAdapterCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllCategoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        if (bundle.getInt("ID", 0) > 0) {
            id = bundle.getInt("ID", 0);
        }

        binding.mTxtToolBarTitle.setText(bundle.getString("category_name",""));

        mSessionUtil = new SessionUtil(mContext);
        mApiCall = new ApiCall();

        binding.mSRVCategoryList.setLayoutManager(new GridLayoutManager(mContext, 1));
        mAdapterCategory = new AdapterViewAllCategory(mContext, categoryList, this);
        binding.mSRVCategoryList.setAdapter(mAdapterCategory);

        binding.mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.mIvCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ActivityCartList.class);
                mContext.startActivity(intent);
            }
        });

        initSuperRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        cartList();
        //initSuperRecyclerView();
    }

    private void initSuperRecyclerView() {
        binding.mSRVCategoryList.removeMoreListener();
        binding.mSRVCategoryList.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                categoryList();
            }
        });
        offset = 0;
        categoryList.clear();
        binding.mSRVCategoryList.showProgress();
        categoryList();
    }

    private void categoryList() {
        try {
            JSONObject object = new JSONObject();
            object.put("limit", limit);
            object.put("offset", offset);
            object.put("category_id", id);

            String data = object.toString();
            PrintLog.d(TAG, data);
            PrintLog.d(TAG, mSessionUtil.getApiToken());
            Call<ResponseBody> call = APIClient.getInstance().getCategoryImageList(mSessionUtil.getApiToken(), data);
            mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        binding.mSRVCategoryList.showRecycler();
                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        if (commonRes.getStatus() == StandardStatusCodes.SUCCESS) {
                            ViewAllCategoryModel model = gson.fromJson(responseData, ViewAllCategoryModel.class);
                            if (model.getData() != null
                                    && model.getData().getCategoryImage().size() > 0) {
                                if (offset == 0) {
                                    categoryList.clear();
                                    categoryList.addAll(model.getData().getCategoryImage());

                                    for (CartListModel.Data data : cartList) {
                                        for (ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList) {
                                            if (data.getCategoryImageId() == categoryImage.getCategoryImageId()) {
                                                categoryImage.setAddedToCart(true);
                                            }
                                        }
                                    }

                                    mAdapterCategory.notifyDataSetChanged();
                                } else {
                                    categoryList.addAll(model.getData().getCategoryImage());
                                    for (CartListModel.Data data : cartList) {
                                        for (ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList) {
                                            if (data.getCategoryImageId() == categoryImage.getCategoryImageId()) {
                                                categoryImage.setAddedToCart(true);
                                            }
                                        }
                                    }
                                    mAdapterCategory.notifyItemRangeInserted(offset, model.getData().getCategoryImage().size());
                                }
                                offset = categoryList.size();

                            } else {
                                binding.mSRVCategoryList.removeMoreListener();
                                mAdapterCategory.notifyDataSetChanged();
                            }
                        } else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                            AppUtil.showToast(mContext, commonRes.getMessage());
                            AppUtil.autoLogout(ActivityViewAllCategory.this);
                        } else {
                            binding.mSRVCategoryList.removeMoreListener();
                            mAdapterCategory.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void failure(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        PrintLog.e(TAG, responseData);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void cartList() {
        try {
            JSONObject object = new JSONObject();
            object.put("limit", "10");
            object.put("offset", "0");

            String data = object.toString();
            PrintLog.d(TAG, data);
            cartList.clear();
            Call<ResponseBody> call = APIClient.getInstance().cartList(mSessionUtil.getApiToken(), data);
            mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {

                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        if (commonRes.getStatus() == StandardStatusCodes.SUCCESS) {
                            CartListModel model = gson.fromJson(responseData, CartListModel.class);
                            if (model.getData() != null
                                    && model.getData().size() > 0) {
                                if (offset == 0) {
                                    cartList.addAll(model.getData());
                                } else {
                                    cartList.addAll(model.getData());
                                }

                                for(ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList){
                                    categoryImage.setAddedToCart(false);
                                }

                                for (CartListModel.Data data : cartList) {
                                    for (ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList) {
                                        if (data.getCategoryImageId() == categoryImage.getCategoryImageId()) {
                                            categoryImage.setAddedToCart(true);
                                        }
                                    }
                                }

                                mAdapterCategory.notifyDataSetChanged();

                                //offset = cartList.size();
                                //initSuperRecyclerView();
                            } else {
                                //initSuperRecyclerView();
                                for(ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList){
                                    categoryImage.setAddedToCart(false);
                                }
                                for (CartListModel.Data data : cartList) {
                                    for (ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList) {
                                        if (data.getCategoryImageId() == categoryImage.getCategoryImageId()) {
                                            categoryImage.setAddedToCart(true);
                                        }
                                    }
                                }

                                mAdapterCategory.notifyDataSetChanged();
                            }
                        } else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                            AppUtil.showToast(mContext, commonRes.getMessage());
                            AppUtil.autoLogout(ActivityViewAllCategory.this);
                           // initSuperRecyclerView();
                            for(ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList){
                                categoryImage.setAddedToCart(false);
                            }
                            for (CartListModel.Data data : cartList) {
                                for (ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList) {
                                    if (data.getCategoryImageId() == categoryImage.getCategoryImageId()) {
                                        categoryImage.setAddedToCart(true);
                                    }
                                }
                            }

                            mAdapterCategory.notifyDataSetChanged();
                        }

                    } else {
                        //initSuperRecyclerView();
                        for(ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList){
                            categoryImage.setAddedToCart(false);
                        }
                        for (CartListModel.Data data : cartList) {
                            for (ViewAllCategoryModel.Data.CategoryImage categoryImage : categoryList) {
                                if (data.getCategoryImageId() == categoryImage.getCategoryImageId()) {
                                    categoryImage.setAddedToCart(true);
                                }
                            }
                        }

                        mAdapterCategory.notifyDataSetChanged();
                    }
                }

                @Override
                public void failure(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        PrintLog.e(TAG, responseData);
                    }
                   // initSuperRecyclerView();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


    @Override
    public void onCategoryClick(ViewAllCategoryModel.Data.CategoryImage itemData) {
        ArrayList<String> items = new ArrayList<>();
        items.add(itemData.getImage());
        Intent intent = new Intent(mContext, ActivityPhotoView.class);
        intent.putExtra(ActivityPhotoView.ITEMS, items);
        startActivity(intent);
    }

    @Override
    public void onAddToCartClick(int position) {
        addToCartCall(position);
    }

    @Override
    public void onDownloadClick(String imgUrl) {
        String fileName = AppUtil.getFileNameFromUrl(imgUrl);
        DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);

        if (downloadManager == null) {
            Toast.makeText(mContext, "Download Manager not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri uri = Uri.parse(imgUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // Set download title and description (appears in notifications)
        request.setTitle("Downloading Image");
        request.setDescription("Downloading " + fileName);

        // Allow network types
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        // Show notification when downloading
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Set the destination path for the downloaded file
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        // Enqueue download
        long downloadId = downloadManager.enqueue(request);
    }

    private void addToCartCall(int position) {
        try {
            JSONObject object = new JSONObject();
            object.put("category_image_id", categoryList.get(position).getCategoryImageId());
            object.put("quantity", "1");

            String data = object.toString();
            PrintLog.d(TAG, data);

            Call<ResponseBody> call = APIClient.getInstance().addToCart(mSessionUtil.getApiToken(), data);
            ApiCall apiCall = new ApiCall();
            apiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        AppUtil.showToast(mContext, commonRes.getMessage());
                        categoryList.get(position).setAddedToCart(commonRes.getStatus() == StandardStatusCodes.SUCCESS);
                        mAdapterCategory.notifyItemChanged(position);
                    }
                }

                @Override
                public void failure(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        PrintLog.e(TAG, responseData);
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}