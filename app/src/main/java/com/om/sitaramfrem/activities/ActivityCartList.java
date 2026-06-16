package com.om.sitaramfrem.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.om.sitaramfrem.adapters.AdapterCart;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.ActivityCartListBinding;
import com.om.sitaramfrem.listners.CartClickListener;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.models.cart_list.CartListModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;
import com.om.superrecyclerview.OnMoreListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActivityCartList extends BaseActivity implements CartClickListener {

    private ActivityCartListBinding binding;
    private Context mContext;
    private static final String TAG = ActivityCartList.class.getSimpleName();

    private SessionUtil mSessionUtil;
    private ApiCall mApiCall;

    private int limit = 10;
    private int offset = 0;

    private List<CartListModel.Data> cartList = new ArrayList<>();
    AdapterCart mAdapterCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartListBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;

        mSessionUtil = new SessionUtil(mContext);
        mApiCall = new ApiCall();

        binding.mSRVCartList.setLayoutManager(new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false));
        mAdapterCart = new AdapterCart(mContext, cartList, mSessionUtil, this);
        binding.mSRVCartList.setAdapter(mAdapterCart);

        binding.mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ActivityCartList.this, ActivityAddOrder.class);
                intent.putExtra("ID", "");
                intent.putExtra("CART_LIST", (Serializable) cartList);
                startActivity(intent);
            }
        });

        binding.mIvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        initSuperRecyclerView();
    }

    private void initSuperRecyclerView() {
        binding.mSRVCartList.removeMoreListener();
        binding.mSRVCartList.setOnMoreListener(new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                cartList();
            }
        });
        offset = 0;
        cartList.clear();
        binding.mSRVCartList.showProgress();
        cartList();
    }

    private void cartList() {
        try {
            JSONObject object = new JSONObject();
            object.put("limit", limit);
            object.put("offset", offset);

            String data = object.toString();
            PrintLog.d(TAG, data);

            Call<ResponseBody> call = APIClient.getInstance().cartList(mSessionUtil.getApiToken(), data);
            mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        binding.mSRVCartList.showRecycler();
                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        if (commonRes.getStatus() == StandardStatusCodes.SUCCESS) {
                            CartListModel model = gson.fromJson(responseData, CartListModel.class);
                            if (model.getData() != null
                                    && model.getData().size() > 0) {
                                if (offset == 0) {
                                    cartList.clear();
                                    cartList.addAll(model.getData());
                                    mAdapterCart.notifyDataSetChanged();
                                } else {
                                    cartList.addAll(model.getData());
                                    mAdapterCart.notifyItemRangeInserted(offset, model.getData().size());
                                }
                                offset = cartList.size();
                            } else {
                                binding.mSRVCartList.removeMoreListener();
                                mAdapterCart.notifyDataSetChanged();
                            }

                            if (cartList.size() > 0) {
                                binding.btnCheckout.setVisibility(View.VISIBLE);
                            }
                        } else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                            AppUtil.showToast(mContext, commonRes.getMessage());
                            AppUtil.autoLogout(ActivityCartList.this);
                        } else {
                            binding.btnCheckout.setVisibility(View.GONE);
                            binding.mSRVCartList.removeMoreListener();
                            mAdapterCart.notifyDataSetChanged();
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

    private void editCartCall(int position, String flag, int qty) {
        try {

            JSONObject object = new JSONObject();
            object.put("category_image_id", cartList.get(position).getCategoryImageId());
            object.put("quantity", qty);
            object.put("flag", flag);

            String data = object.toString();
            PrintLog.d(TAG, data);

            Call<ResponseBody> call = APIClient.getInstance().editCart(mSessionUtil.getApiToken(), data);
            ApiCall apiCall = new ApiCall();
            apiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        //AppUtil.showToast(mContext, commonRes.getMessage());
                        cartList.get(position).setQuantity(String.valueOf(qty));
                        mAdapterCart.notifyItemChanged(position);
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

    private void removeFromCartCall(int position) {
        try {
            JSONObject object = new JSONObject();
            object.put("category_image_id", cartList.get(position).getCategoryImageId());
            object.put("cart_id", cartList.get(position).getCartId());

            String data = object.toString();
            PrintLog.d(TAG, data);

            Call<ResponseBody> call = APIClient.getInstance().deleteFromCart(mSessionUtil.getApiToken(), data);
            ApiCall apiCall = new ApiCall();
            apiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        AppUtil.showToast(mContext, commonRes.getMessage());
                        CartListModel model = gson.fromJson(responseData, CartListModel.class);
                        cartList.clear();
                        cartList.addAll(model.getData());
                        mAdapterCart.notifyDataSetChanged();

                        if (cartList.size() > 0) {
                            binding.btnCheckout.setVisibility(View.VISIBLE);
                        } else {
                            binding.btnCheckout.setVisibility(View.GONE);
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

    private void clearCartCall() {
        try {
            JSONObject object = new JSONObject();
            object.put("cart_id", "");

            String data = object.toString();
            PrintLog.d(TAG, data);

            Call<ResponseBody> call = APIClient.getInstance().clearCart(mSessionUtil.getApiToken(), data);
            ApiCall apiCall = new ApiCall();
            apiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    finish();
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
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onRemoveCartClick(int position) {
        removeFromCartCall(position);
    }

    @Override
    public void onEditCartClick(int position, String flag, int qty) {
        editCartCall(position, flag, qty);
    }
}