package com.om.sitaramfrem.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.om.sitaramfrem.activities.ActivityPhotoView;
import com.om.sitaramfrem.activities.ActivityViewAllCategory;
import com.om.sitaramfrem.adapters.AdapterInActiveCategory;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.FragmentInActiveCategoryBinding;
import com.om.sitaramfrem.listners.InActiveCategoryClickListener;
import com.om.sitaramfrem.models.inactive_category.InActiveCategoryModel;
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

public class FragmentInActiveCategory extends Fragment implements InActiveCategoryClickListener {
    private Context mContext;
    private FragmentInActiveCategoryBinding binding;
    private SessionUtil mSessionUtil;
    private ApiCall mApiCall;

    private int limit = 10;
    private int offset = 0;
    private List<InActiveCategoryModel.Data> categoryList = new ArrayList();
    AdapterInActiveCategory mAdapter;
    private static final String TAG = FragmentInActiveCategory.class.getSimpleName();

    public FragmentInActiveCategory() {
        // Required empty public constructor
    }

    public static FragmentInActiveCategory newInstance() {
        FragmentInActiveCategory fragment = new FragmentInActiveCategory();
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
        binding = FragmentInActiveCategoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mSessionUtil = new SessionUtil(mContext);
        mApiCall = new ApiCall();

        binding.mSRVList.setLayoutManager(new GridLayoutManager(mContext, 2));
        mAdapter = new AdapterInActiveCategory(mContext, categoryList, this);
        binding.mSRVList.setAdapter(mAdapter);

        initSuperRecyclerView();
    }

    private void initSuperRecyclerView() {
        binding.mSRVList.removeMoreListener();
        binding.mSRVList.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                inActiveCategoryList();
            }
        });
        offset = 0;
        categoryList.clear();
        binding.mSRVList.showProgress();
        inActiveCategoryList();
    }

    private void inActiveCategoryList() {
        try {
            JSONObject object = new JSONObject();
            object.put("limit", limit);
            object.put("offset", offset);
            String data = object.toString();
            PrintLog.d(TAG, data);
            Call<ResponseBody> call = APIClient.getInstance().getInActiveCategoryImageList(mSessionUtil.getApiToken(), data);
            mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        binding.mSRVList.showRecycler();
                        Gson gson = new Gson();
                        InActiveCategoryModel model = gson.fromJson(responseData,InActiveCategoryModel.class);
                        if(model.getStatus() == StandardStatusCodes.SUCCESS){
                            if (model.getData() != null
                                    && model.getData().size() > 0) {
                                if (offset == 0) {
                                    categoryList.clear();
                                    categoryList.addAll(model.getData());
                                    mAdapter.notifyDataSetChanged();
                                }else{
                                    categoryList.addAll(model.getData());
                                    mAdapter.notifyItemRangeInserted(offset, model.getData().size());
                                }
                                offset = categoryList.size();
                            }else{
                                binding.mSRVList.removeMoreListener();
                                mAdapter.notifyDataSetChanged();
                            }
                        } else if (model.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                            AppUtil.showToast(mContext, model.getMessage());
                            AppUtil.autoLogout(getActivity());
                        } else {
                            binding.mSRVList.removeMoreListener();
                            mAdapter.notifyDataSetChanged();
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

    @Override
    public void onCategoryClick(InActiveCategoryModel.Data itemData) {
        ArrayList<String> items = new ArrayList<>();
        items.add(itemData.getImage());
        Intent intent = new Intent(mContext, ActivityPhotoView.class);
        intent.putExtra(ActivityPhotoView.ITEMS, items);
        startActivity(intent);
    }
}