package com.om.sitaramfrem.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.adapters.AdapterReviewOrderList;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.ActivityReviewOrderBinding;
import com.om.sitaramfrem.event.UpdateOrderList;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.models.OrderModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.CustomDialog;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActivityReviewOrder extends BaseActivity {
    private String id = "";
    private ArrayList<OrderModel> orderModels = new ArrayList<>();
    private ActivityReviewOrderBinding binding;
    private Context mContext;
    private CustomDialog dialog;
    private SessionUtil mSessionUtil;
    public static final String ID = "id";
    public static final String ORDER_MODEL = "orderModel";
    private static final String TAG = ActivityReviewOrder.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewOrderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        if (bundle.getParcelableArrayList(ORDER_MODEL) != null) {
            orderModels.addAll(bundle.getParcelableArrayList(ORDER_MODEL));
        }
        dialog = new CustomDialog();
        mSessionUtil = new SessionUtil(mContext);

        binding.mRvReviewOrderList.setLayoutManager(new LinearLayoutManager(mContext));
        binding.mRvReviewOrderList.addItemDecoration(new DividerItemDecoration(binding.mRvReviewOrderList.getContext(), DividerItemDecoration.VERTICAL));
        AdapterReviewOrderList mAdapterReviewOrderList = new AdapterReviewOrderList(mContext, orderModels);
        binding.mRvReviewOrderList.setAdapter(mAdapterReviewOrderList);

        binding.mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderModels != null && orderModels.size() > 0) {
                    new CreateDataForOrder().execute();
                }
            }
        });

        binding.mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private class CreateDataForOrder extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.displayProgress(mContext);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String strJson = "";
            try {
                JSONObject object = new JSONObject();
                JSONArray orderJsonArray = new JSONArray();
                for (int i = 0; i < orderModels.size(); i++) {
                    JSONObject itemObj = new JSONObject();
                    itemObj.put("frame_id", orderModels.get(i).getFrameNo());
                    itemObj.put("item_id", orderModels.get(i).getItemId());
                    itemObj.put("frame_measure", orderModels.get(i).getFrameMeasure());
                    itemObj.put("quantity", orderModels.get(i).getQuantity());
                    itemObj.put("notes", orderModels.get(i).getNotes());
                    itemObj.put("image", AppUtil.encodeToBase64(mContext, orderModels.get(i).getImgUri()));
                    itemObj.put("category_image_id", orderModels.get(i).getCategoryImageId());
                    orderJsonArray.put(itemObj);
                }
                object.put("attribute_array", orderJsonArray);
                strJson = object.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return strJson;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (ValidationUtil.validateString(s)) {
                addOrder(s);
            } else {
                dialog.dismissProgress(ActivityReviewOrder.this);
            }
        }
    }

    private void addOrder(String data) {
        PrintLog.d(TAG, data);
        Call<ResponseBody> call = APIClient.getInstance().addOrder(mSessionUtil.getApiToken(), data);
        ApiCall mApiCall = new ApiCall();
        mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                dialog.dismissProgress(ActivityReviewOrder.this);
                if (ValidationUtil.validateString(responseData)) {
                    Gson gson = new Gson();
                    CommonRes model = gson.fromJson(responseData, CommonRes.class);
                    if (ValidationUtil.validateString(model.getMessage())) {
                        AppUtil.showToast(mContext, model.getMessage());
                    }

                    if (model.getStatus() == StandardStatusCodes.SUCCESS) {
                        EventBus.getDefault().post(new UpdateOrderList());
                        setResult(RESULT_OK);

                        Intent intent = new Intent(mContext, ActivityHome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else if (model.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                        AppUtil.autoLogout(ActivityReviewOrder.this);
                    }
                }
            }

            @Override
            public void failure(String responseData) {
                dialog.dismissProgress(ActivityReviewOrder.this);
                if (ValidationUtil.validateString(responseData)) {
                    PrintLog.e(TAG, responseData);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}