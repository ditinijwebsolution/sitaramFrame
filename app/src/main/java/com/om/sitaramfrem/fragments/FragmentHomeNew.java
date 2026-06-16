package com.om.sitaramfrem.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.activities.ActivityViewAllCategory;
import com.om.sitaramfrem.adapters.AdapterFragmentPager;
import com.om.sitaramfrem.adapters.AdapterHome;
import com.om.sitaramfrem.adapters.AdapterOrderList;
import com.om.sitaramfrem.adapters.AdapterSlider;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.FragmentHomeBinding;
import com.om.sitaramfrem.databinding.FragmentHomeNewBinding;
import com.om.sitaramfrem.listners.HomeClickListener;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.models.cart_list.CartListModel;
import com.om.sitaramfrem.models.categorylist.CategoryModel;
import com.om.sitaramfrem.models.orderlist.OrderModel;
import com.om.sitaramfrem.models.slider.SliderData;
import com.om.sitaramfrem.models.slider.SliderModel;
import com.om.sitaramfrem.models.view_all_category.ViewAllCategoryModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;
import com.om.superrecyclerview.OnMoreListener;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class FragmentHomeNew extends Fragment implements HomeClickListener {
    private FragmentHomeNewBinding binding;
    private Context mContext;

    private AdapterSlider mAdapterSliderList;
    private List<SliderModel.Data> sliderList = new ArrayList<>();
    private List<CategoryModel.Data> categoryList = new ArrayList<>();
    private AdapterHome mAdapterHome;

    private SessionUtil mSessionUtil;
    private ApiCall mApiCall;

    private int limit = 10;
    private int offset = 0;

    private List<CartListModel.Data> cartList = new ArrayList<>();

    private static final String TAG = FragmentCompletedOrder.class.getSimpleName();

    //HomeClickListener homeClickListener;

    public FragmentHomeNew() {
        // Required empty public constructor
    }

    public static FragmentHomeNew newInstance() {
        FragmentHomeNew fragment = new FragmentHomeNew();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeNewBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSessionUtil = new SessionUtil(mContext);
        mApiCall = new ApiCall();

        binding.recHome.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapterHome = new AdapterHome(mContext, categoryList, this);
        binding.recHome.setAdapter(mAdapterHome);

        sliderList();
        //initSuperRecyclerView();
        //categoryList();
    }


    private void sliderList() {
        try {

            Call<ResponseBody> call = APIClient.getInstance().sliderList();
            mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {

                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        if (commonRes.getStatus() == StandardStatusCodes.SUCCESS) {
                            SliderModel model = gson.fromJson(responseData, SliderModel.class);
                            if (model.getData() != null
                                    && model.getData().size() > 0) {
                                sliderList.clear();
                                sliderList.addAll(model.getData());

                                mAdapterSliderList = new AdapterSlider(mContext, sliderList);
                                binding.slider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_RTL);
                                binding.slider.setSliderAdapter(mAdapterSliderList);
                                binding.slider.setScrollTimeInSec(5);
                                binding.slider.setAutoCycle(true);
                                binding.slider.startAutoCycle();
                                binding.slider.setVisibility(View.VISIBLE);

                            } else {
                                binding.slider.setVisibility(View.GONE);
                            }
                        } else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                            AppUtil.showToast(mContext, commonRes.getMessage());
                            AppUtil.autoLogout(getActivity());
                            binding.slider.setVisibility(View.GONE);
                        } else {
                            binding.slider.setVisibility(View.GONE);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*private void initSuperRecyclerView() {
        binding.mSRVHome.removeMoreListener();
        categoryList.clear();
        binding.mSRVHome.showProgress();
        categoryList();
    }*/

    private void categoryList() {
        try {

            Call<ResponseBody> call = APIClient.getInstance().categoryList();
            mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {

                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        if (commonRes.getStatus() == StandardStatusCodes.SUCCESS) {
                            CategoryModel model = gson.fromJson(responseData, CategoryModel.class);
                            if (model.getData() != null
                                    && model.getData().size() > 0) {
                                categoryList.clear();
                                categoryList.addAll(model.getData());

                                for (CartListModel.Data data : cartList) {
                                    for (CategoryModel.Data categoryMainImage : categoryList) {
                                        for (CategoryModel.Data.CategoryImage categoryImage : categoryMainImage.getCategoryImage()) {
                                            if (data.getCategoryImageId() == categoryImage.getCategoryImageId()) {
                                                categoryImage.setAddedToCart(true);
                                            }
                                        }
                                    }
                                }
                                mAdapterHome.notifyDataSetChanged();

                            } else {
                                mAdapterHome.notifyDataSetChanged();
                            }
                        } else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                            AppUtil.showToast(mContext, commonRes.getMessage());
                            AppUtil.autoLogout(getActivity());

                        } else {
                            mAdapterHome.notifyDataSetChanged();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cartList();
        //initSuperRecyclerView();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    @Override
    public void onAddToCartClick(int mainPosition, int position) {
        addToCartCall(mainPosition, position);
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

    private void addToCartCall(int mainPosition, int position) {
        try {
            JSONObject object = new JSONObject();
            object.put("category_image_id", categoryList.get(mainPosition).getCategoryImage().get(position).getCategoryImageId());
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
                        categoryList.get(mainPosition).getCategoryImage().get(position).setAddedToCart(commonRes.getStatus() == StandardStatusCodes.SUCCESS);
                        mAdapterHome.notifyItemChanged(mainPosition);
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
                                offset = cartList.size();
                                categoryList();
                            } else {
                                categoryList();
                            }
                        } else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                            AppUtil.showToast(mContext, commonRes.getMessage());
                            AppUtil.autoLogout(getActivity());
                            categoryList();
                        }

                    } else {
                        categoryList();
                    }
                }

                @Override
                public void failure(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        PrintLog.e(TAG, responseData);
                    }
                    categoryList();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}