package com.om.sitaramfrem.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.om.sitaramfrem.adapters.AdapterOrderList;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.FragmentCompletedOrderBinding;
import com.om.sitaramfrem.databinding.FragmentInprogressOrderBinding;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.models.orderlist.OrderModel;
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

public class FragmentCompletedOrder extends Fragment {
    private Context mContext;
    private FragmentCompletedOrderBinding binding;
    private SessionUtil mSessionUtil;
    private ApiCall mApiCall;

    private AdapterOrderList mAdapterOrderList;
    private List<OrderModel.Data> dataList = new ArrayList<>();

    private int limit = 10;
    private int offset = 0;

    private static final String TAG = FragmentCompletedOrder.class.getSimpleName();

    public FragmentCompletedOrder() {
        // Required empty public constructor
    }

    public static FragmentCompletedOrder newInstance() {
        FragmentCompletedOrder fragment = new FragmentCompletedOrder();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCompletedOrderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSessionUtil = new SessionUtil(mContext);
        mApiCall = new ApiCall();

        binding.mSRVOrderList.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapterOrderList = new AdapterOrderList(mContext, dataList);
        binding.mSRVOrderList.setAdapter(mAdapterOrderList);

    }
    @Override
    public void onResume() {
        super.onResume();
        initSuperRecyclerView();
    }

    private void initSuperRecyclerView() {
        binding.mSRVOrderList.removeMoreListener();
        binding.mSRVOrderList.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                orderList();
            }
        });
        offset = 0;
        dataList.clear();
        binding.mSRVOrderList.showProgress();
        orderList();
    }

    private void orderList() {
        try {
            JSONObject object = new JSONObject();
            object.put("limit", limit);
            object.put("offset", offset);
            object.put("status", "completed");

            String data = object.toString();
            PrintLog.d(TAG, data);
            Call<ResponseBody> call = APIClient.getInstance().orderList(mSessionUtil.getApiToken(), data);
            mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        binding.mSRVOrderList.showRecycler();
                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        if (commonRes.getStatus() == StandardStatusCodes.SUCCESS) {
                            OrderModel model = gson.fromJson(responseData, OrderModel.class);
                            if (model.getData() != null
                                    && model.getData().size() > 0) {
                                if (offset == 0) {
                                    dataList.clear();
                                    dataList.addAll(model.getData());
                                    mAdapterOrderList.notifyDataSetChanged();
                                } else {
                                    dataList.addAll(model.getData());
                                    mAdapterOrderList.notifyItemRangeInserted(offset, model.getData().size());
                                }
                                offset = dataList.size();
                            } else {
                                binding.mSRVOrderList.removeMoreListener();
                                mAdapterOrderList.notifyDataSetChanged();
                            }
                        }else if(commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE){
                            AppUtil.showToast(mContext,commonRes.getMessage());
                            AppUtil.autoLogout(getActivity());
                        } else {
                            binding.mSRVOrderList.removeMoreListener();
                            mAdapterOrderList.notifyDataSetChanged();
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
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}