package com.om.sitaramfrem.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.om.filepicker.Constants;
import com.om.filepicker.FilePickerController;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.adapters.CustomSpinnerAdapter;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.ActivityAddOrderBinding;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.models.OrderModel;
import com.om.sitaramfrem.models.cart_list.CartListModel;
import com.om.sitaramfrem.models.frame_list.FrameItemModel;
import com.om.sitaramfrem.models.item.ItemModel;
import com.om.sitaramfrem.models.measurement_list.MeasurementModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.CustomDialog;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;
import com.om.sitaramfrem.view.CustomTextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import pl.utkala.searchablespinner.SearchableSpinner;
import retrofit2.Call;

public class ActivityAddOrder extends BaseActivity implements View.OnClickListener {

    private ActivityAddOrderBinding binding;
    private Context mContext;
    private SessionUtil mSessionUtil;
    private ApiCall mApiCall;
    private ArrayList<String> itemList = new ArrayList<>();
    private List<ItemModel.Data> dataList = new ArrayList<>();
    private List<FrameItemModel.Data> frameList = new ArrayList<>();
    private ArrayList<String> frameStrList = new ArrayList<>();
    private FilePickerController mFilePickerController;

    private ArrayList<OrderModel> orderModels = new ArrayList<>();
    private CustomDialog dialog;
    private static final String TAG = ActivityAddOrder.class.getSimpleName();

    private String id = "";

    private List<CartListModel.Data> cartList = new ArrayList<>();
    private List<MeasurementModel.Data> measurementList = new ArrayList<>();
    private ArrayList<String> measurementNameList = new ArrayList<>();

    private int limit = 10;
    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddOrderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }
        if (!bundle.getString("ID").equalsIgnoreCase("")) {
            id = bundle.getString("ID");
        }

        if (bundle.getSerializable("CART_LIST") != null) {
            cartList = (List<CartListModel.Data>) bundle.getSerializable("CART_LIST");
        }

        mSessionUtil = new SessionUtil(mContext);
        mApiCall = new ApiCall();
        dialog = new CustomDialog();

        binding.mIvBack.setOnClickListener(this);
        binding.mBtnAddMore.setOnClickListener(this);
        binding.mBtnAdd.setOnClickListener(this);
        //binding.mEdtDate.setOnClickListener(this);
        //binding.mEdtTime.setOnClickListener(this);

        getFrameNoList();

        try {
            mFilePickerController = new FilePickerController(this);
            mFilePickerController.setAllowTakePhoto(true);
            mFilePickerController.setAllowFileManager(true);
            mFilePickerController.setAllowCroppingImage(true);
            mFilePickerController.setAllowFrontFacingCamera(false);
            mFilePickerController.setAllowMultipleSelection(false);
            mFilePickerController.setAllowMimeTypes(Constants.IMAGE_MIME_TYPES);
            mFilePickerController.setScaleQuality(30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mFilePickerController.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 501 && resultCode == Activity.RESULT_OK) {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mFilePickerController.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == binding.mBtnAddMore) {
            addMoreLayout(null);
        }/*else if(view == binding.mEdtDate){
            openDatePicker();
        }else if(view == binding.mEdtTime){
            openTimePicker();
        }*/ else if (view == binding.mIvBack) {
            onBackPressed();
        } else if (view == binding.mBtnAdd) {
            if (isValidForm()) {
                createOrderModel();
                if (orderModels.size() > 0) {
                    Intent intent = new Intent(mContext, ActivityReviewOrder.class);
                    intent.putParcelableArrayListExtra(ActivityReviewOrder.ORDER_MODEL, orderModels);
                    startActivityForResult(intent, 501);
                }
                //new CreateDataForOrder().execute();
            }
        }
    }

    private void addMoreLayout(CartListModel.Data data) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View view = inflater.inflate(R.layout.view_item_layout, null);
            ImageView mIvFrameImg = view.findViewById(R.id.mIvFrameImg);

            SearchableSpinner mSpinnerFrameNo = view.findViewById(R.id.mSpinnerFrameNo);

            CustomSpinnerAdapter adapterFrameNo = new CustomSpinnerAdapter(mContext, R.layout.row_item, frameStrList);
            adapterFrameNo.setDropDownViewResource(R.layout.row_item_spinner_text);
            mSpinnerFrameNo.setAdapter(adapterFrameNo);

            Spinner mSpinnerItem = view.findViewById(R.id.mSpinnerItem);
            CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(mContext, R.layout.row_item, itemList);
            adapter.setDropDownViewResource(R.layout.row_item_spinner_text);
            mSpinnerItem.setAdapter(adapter);

            Spinner mSpinnerMeasure = view.findViewById(R.id.mSpinnerMeasure);
            CustomSpinnerAdapter adapterMeasure = new CustomSpinnerAdapter(mContext, R.layout.row_item, measurementNameList);
            adapterMeasure.setDropDownViewResource(R.layout.row_item_spinner_text);
            mSpinnerMeasure.setAdapter(adapterMeasure);

            CustomTextInputLayout mTilFrameMeasure = view.findViewById(R.id.mTilFrameMeasure);
            EditText mEdtFrameMeasure = view.findViewById(R.id.mEdtFrameMeasure);

            mSpinnerMeasure.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String selectedItem = adapterView.getItemAtPosition(i).toString();
                    if (selectedItem.equalsIgnoreCase("custom")) {
                        mTilFrameMeasure.setVisibility(View.VISIBLE);
                        mEdtFrameMeasure.setText("");
                    } else {
                        mEdtFrameMeasure.setText(selectedItem);
                        mTilFrameMeasure.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            EditText mEdtQty = view.findViewById(R.id.mEdtQty);

            mSpinnerFrameNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Glide.with(mContext)
                            .load(frameList.get(i).getCategoryImage())
                            .centerCrop()
                            .into(mIvFrameImg);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            /*mIvFrameImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(data==null){
                        mFilePickerController.setFilePickerListener(new FilePickerController.FilePickerListener() {
                            @Override
                            public void onSuccess(ArrayList<Uri> mSelectedFileList) {
                                if (mSelectedFileList != null && mSelectedFileList.size() > 0) {
                                    mIvFrameImg.setImageURI(mSelectedFileList.get(0));
                                    model.setImgUri(mSelectedFileList.get(0));

                                   // Glide.with(mContext).asBitmap().load(mSelectedFileList.get(0)).submit().get();
                                }
                            }
                        });

                        mFilePickerController.showFilePicker();
                    }
                }
            });*/

            Button mBtnRemove = view.findViewById(R.id.mBtnRemove);
            mBtnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = binding.mLinearItemContainer.indexOfChild(view);
                    orderModels.remove(index);
                    binding.mLinearItemContainer.removeViewAt(index);
                    showRemoveButtonLayout();
                }
            });

            OrderModel model = new OrderModel();

            if(data!=null) {
                model.setImgPath(data.getImage());
                Glide.with(this)
                        .load(data.getImage())
                        .centerCrop()
                        .into(mIvFrameImg);
                //mEdtFrameNo.setText(data.getFrameNo());
                mEdtQty.setText(data.getQuantity());
                mEdtQty.setTag(String.valueOf(data.getCategoryImageId()));
                for(int i=0;i<frameList.size();i++){
                    FrameItemModel.Data d = frameList.get(i);
                    if(d.getId()!=null && d.getId().equals(String.valueOf(data.getCategoryImageId()))){
                        mSpinnerFrameNo.setSelection(i);
                        break;
                    }
                }
            }
            orderModels.add(model);
            binding.mLinearItemContainer.addView(view);

            showRemoveButtonLayout();
        }
    }

    private void showRemoveButtonLayout() {
        for (int i = 0; i < binding.mLinearItemContainer.getChildCount(); i++) {
            View view = binding.mLinearItemContainer.getChildAt(i);
            Button mBtnRemove = view.findViewById(R.id.mBtnRemove);
            if (mBtnRemove != null) {
                if (binding.mLinearItemContainer.getChildCount() == 1) {
                    mBtnRemove.setVisibility(View.GONE);
                } else {
                    mBtnRemove.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void itemList() {
        Call<ResponseBody> call = APIClient.getInstance().itemList(mSessionUtil.getApiToken());
        mApiCall.makeApiCall(mContext, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                if (ValidationUtil.validateString(responseData)) {
                    Gson gson = new Gson();
                    CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                    if (commonRes.getStatus() == StandardStatusCodes.SUCCESS) {
                        ItemModel model = gson.fromJson(responseData, ItemModel.class);
                        if (model.getData() != null
                                && model.getData().size() > 0) {
                            itemList.clear();
                            dataList.clear();

                            dataList.addAll(model.getData());

                            for (ItemModel.Data data : dataList) {
                                itemList.add(data.getName());
                            }

                            measurementList();
                            //addMoreLayout();
                        }
                    } else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                        AppUtil.showToast(mContext, commonRes.getMessage());
                        AppUtil.autoLogout(ActivityAddOrder.this);
                    } else {
                        AppUtil.showToast(mContext, commonRes.getMessage());
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
    }

    private void getFrameNoList(){
        Call<ResponseBody> call = APIClient.getInstance().getFrameNoList(mSessionUtil.getApiToken());
        mApiCall.makeApiCall(mContext, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                if (ValidationUtil.validateString(responseData)) {
                    Gson gson = new Gson();
                    CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                    if(commonRes.getStatus() == StandardStatusCodes.SUCCESS){
                        FrameItemModel model = gson.fromJson(responseData, FrameItemModel.class);
                        if (model!=null && model.getData() != null
                                && model.getData().size() > 0) {
                            frameList.clear();
                            frameList.addAll(model.getData());
                            frameStrList.clear();
                            for(FrameItemModel.Data item : model.getData()){
                                frameStrList.add(item.getFrameNumber());
                            }
                        }

                    }else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                        AppUtil.showToast(mContext, commonRes.getMessage());
                        AppUtil.autoLogout(ActivityAddOrder.this);
                    } else {
                        AppUtil.showToast(mContext, commonRes.getMessage());
                    }
                }
                itemList();
            }

            @Override
            public void failure(String responseData) {
                if (ValidationUtil.validateString(responseData)) {
                    PrintLog.e(TAG, responseData);
                }
                itemList();
            }
        });
    }

    private void measurementList() {
        try {
            JSONObject object = new JSONObject();
            object.put("limit", limit);
            object.put("offset", offset);

            String data = object.toString();
            PrintLog.d(TAG, data);

            Call<ResponseBody> call = APIClient.getInstance().measurementList(mSessionUtil.getApiToken(), data);
            mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {

                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        if (commonRes.getStatus() == StandardStatusCodes.SUCCESS) {
                            MeasurementModel model = gson.fromJson(responseData, MeasurementModel.class);
                            if (model.getData() != null
                                    && model.getData().size() > 0) {
                                if (offset == 0) {
                                    measurementList.clear();
                                    measurementList.addAll(model.getData());
                                } else {
                                    measurementList.addAll(model.getData());
                                }

                                for (MeasurementModel.Data data : measurementList) {
                                    measurementNameList.add(data.getName());
                                }

                                offset = measurementList.size();

                                if (cartList.size() > 0) {
                                    addViews();
                                } else {
                                    addMoreLayout(null);
                                }
                            }
                        } else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                            AppUtil.showToast(mContext, commonRes.getMessage());
                            AppUtil.autoLogout(ActivityAddOrder.this);
                        } else {
                           /* binding.mSRVCartList.removeMoreListener();
                            mAdapterCart.notifyDataSetChanged();*/
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

    private void addViews() {
        for (CartListModel.Data data : cartList) {
            addMoreLayout(data);
        }
    }

    /*private void openDatePicker() {
        mAppUtil.hideSoftKeyboard(0);
        Calendar calendar = Calendar.getInstance();
        if (ValidationUtil.validateString(binding.mEdtDate.getText().toString())) {
            calendar = DateTimeUtil.getCalendarFromString(binding.mEdtDate.getText().toString(), DateTimeUtil.DISPLAY_DATE_FORMAT);
        }
        DatePickerDialog dpd = new DatePickerDialog(mContext,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        Calendar mSelectCalendar = Calendar.getInstance();
                        mSelectCalendar.set(Calendar.YEAR, year);
                        mSelectCalendar.set(Calendar.MONTH, monthOfYear);
                        mSelectCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        binding.mEdtDate.setText(DateTimeUtil.getStringFromCalendar(mSelectCalendar, DateTimeUtil.DISPLAY_DATE_FORMAT));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.show();
    }

    private void openTimePicker() {
        mAppUtil.hideSoftKeyboard(0);
        Calendar calendar = Calendar.getInstance();
        if (ValidationUtil.validateString(binding.mEdtTime.getText().toString())) {
            calendar = DateTimeUtil.getCalendarFromString(binding.mEdtTime.getText().toString(), DateTimeUtil.DISPLAY_TIME_FORMAT);
        }
        TimePickerDialog tpd = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar mSelectCalendar = Calendar.getInstance();
                mSelectCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mSelectCalendar.set(Calendar.MINUTE, minute);
                binding.mEdtTime.setText(DateTimeUtil.getStringFromCalendar(mSelectCalendar, DateTimeUtil.DISPLAY_TIME_FORMAT));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        tpd.show();
    }*/

    private boolean isValidForm() {
        if (binding.mLinearItemContainer.getChildCount() > 0) {
            for (int i = 0; i < binding.mLinearItemContainer.getChildCount(); i++) {
                View view = binding.mLinearItemContainer.getChildAt(i);

                CustomTextInputLayout mTilFrameNo = view.findViewById(R.id.mTilFrameNo);
                Spinner mSpinnerFrameNo = view.findViewById(R.id.mSpinnerFrameNo);
                mTilFrameNo.setErrorEnabled(false);
                if (mSpinnerFrameNo.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
                    mTilFrameNo.setError(mContext.getString(R.string.err_enter_frame_number));
                    return false;
                }

                CustomTextInputLayout mTilItem = view.findViewById(R.id.mTilItem);
                Spinner mSpinnerItem = view.findViewById(R.id.mSpinnerItem);
                mTilItem.setErrorEnabled(false);
                if (mSpinnerItem.getSelectedItemPosition() == Spinner.INVALID_POSITION) {
                    mTilItem.setError(mContext.getString(R.string.err_select_item));
                    return false;
                }

                CustomTextInputLayout mTilFrameMeasure = view.findViewById(R.id.mTilFrameMeasure);
                EditText mEdtFrameMeasure = view.findViewById(R.id.mEdtFrameMeasure);
                mTilFrameMeasure.setErrorEnabled(false);
                if (ValidationUtil.validateString(mEdtFrameMeasure.getText().toString()) == false) {
                    mEdtFrameMeasure.requestFocus();
                    mTilFrameMeasure.setError(mContext.getString(R.string.err_enter_frame_measure));
                    return false;
                }

                CustomTextInputLayout mTilQty = view.findViewById(R.id.mTilQty);
                EditText mEdtQty = view.findViewById(R.id.mEdtQty);
                mTilQty.setErrorEnabled(false);
                if (ValidationUtil.validateString(mEdtQty.getText().toString()) == false) {
                    mEdtQty.requestFocus();
                    mTilQty.setError(mContext.getString(R.string.err_enter_quantity));
                    return false;
                } else {
                    try {
                        int qty = Integer.parseInt(mEdtQty.getText().toString());
                        if (qty < 1) {
                            mEdtQty.requestFocus();
                            mTilQty.setError(mContext.getString(R.string.err_enter_valid_quantity));
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /*CustomTextInputLayout mTilNotes = view.findViewById(R.id.mTilNotes);
                EditText mEdtNotes = view.findViewById(R.id.mEdtNotes);
                mTilNotes.setErrorEnabled(false);
                if (ValidationUtil.validateString(mEdtNotes.getText().toString()) == false) {
                    mEdtNotes.requestFocus();
                    mTilNotes.setError(mContext.getString(R.string.err_enter_notes));
                    return false;
                }*/

            }
            return true;
        }
        return false;
    }

    private void createOrderModel() {
        for (int i = 0; i < binding.mLinearItemContainer.getChildCount(); i++) {

            View view = binding.mLinearItemContainer.getChildAt(i);
            Spinner mSpinnerFrameNo = view.findViewById(R.id.mSpinnerFrameNo);
            if (frameList.size() > 0 && mSpinnerFrameNo.getSelectedItemPosition() < frameList.size()) {
                orderModels.get(i).setFrameNo(frameList.get(mSpinnerFrameNo.getSelectedItemPosition()).getId());
                orderModels.get(i).setFrameName(frameList.get(mSpinnerFrameNo.getSelectedItemPosition()).getFrameNumber());
                orderModels.get(i).setImgPath(frameList.get(mSpinnerFrameNo.getSelectedItemPosition()).getCategoryImage());
                orderModels.get(i).setCategoryImageId(frameList.get(mSpinnerFrameNo.getSelectedItemPosition()).getId());
            }

            Spinner mSpinnerItem = view.findViewById(R.id.mSpinnerItem);
            if (dataList.size() > 0 && mSpinnerItem.getSelectedItemPosition() < dataList.size()) {
                orderModels.get(i).setItemId(dataList.get(mSpinnerItem.getSelectedItemPosition()).getId());
                orderModels.get(i).setItemName(dataList.get(mSpinnerItem.getSelectedItemPosition()).getName());
            }

            EditText mEdtFrameMeasure = view.findViewById(R.id.mEdtFrameMeasure);
            orderModels.get(i).setFrameMeasure(mEdtFrameMeasure.getText().toString());

            EditText mEdtQty = view.findViewById(R.id.mEdtQty);
            orderModels.get(i).setQuantity(mEdtQty.getText().toString());
            if(mEdtQty.getTag()!=null) {
                orderModels.get(i).setCategoryImageId(mEdtQty.getTag().toString());
            }

            EditText mEdtNotes = view.findViewById(R.id.mEdtNotes);
            orderModels.get(i).setNotes(mEdtNotes.getText().toString());
        }
    }

    /*private JSONObject getItemData() {
        JSONObject object = new JSONObject();
        try {
            JSONArray orderJsonArray = new JSONArray();
            for (int i = 0; i < binding.mLinearItemContainer.getChildCount(); i++) {
                JSONObject itemObj = new JSONObject();

                View view = binding.mLinearItemContainer.getChildAt(i);
                EditText mEdtFrameNo = view.findViewById(R.id.mEdtFrameNo);
                itemObj.put("frame_no", mEdtFrameNo.getText().toString());

                Spinner mSpinnerItem = view.findViewById(R.id.mSpinnerItem);
                if (dataList.size() > 0 && mSpinnerItem.getSelectedItemPosition() < dataList.size()) {
                    itemObj.put("item_id", dataList.get(mSpinnerItem.getSelectedItemPosition()).getId());
                }

                EditText mEdtFrameMeasure = view.findViewById(R.id.mEdtFrameMeasure);
                itemObj.put("frame_measure", mEdtFrameMeasure.getText().toString());

                EditText mEdtQty = view.findViewById(R.id.mEdtQty);
                itemObj.put("quantity", mEdtQty.getText().toString());

                EditText mEdtNotes = view.findViewById(R.id.mEdtNotes);
                itemObj.put("notes", mEdtNotes.getText().toString());

                orderJsonArray.put(itemObj);
            }
            object.put("attribute_array", orderJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }*/

    /*private class CreateDataForOrder extends AsyncTask<Void, Void, String> {

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
                for (int i = 0; i < binding.mLinearItemContainer.getChildCount(); i++) {
                    JSONObject itemObj = new JSONObject();

                    View view = binding.mLinearItemContainer.getChildAt(i);
                    EditText mEdtFrameNo = view.findViewById(R.id.mEdtFrameNo);
                    itemObj.put("frame_no", mEdtFrameNo.getText().toString());

                    Spinner mSpinnerItem = view.findViewById(R.id.mSpinnerItem);
                    if (dataList.size() > 0 && mSpinnerItem.getSelectedItemPosition() < dataList.size()) {
                        itemObj.put("item_id", dataList.get(mSpinnerItem.getSelectedItemPosition()).getId());
                    }

                    EditText mEdtFrameMeasure = view.findViewById(R.id.mEdtFrameMeasure);
                    itemObj.put("frame_measure", mEdtFrameMeasure.getText().toString());

                    EditText mEdtQty = view.findViewById(R.id.mEdtQty);
                    itemObj.put("quantity", mEdtQty.getText().toString());

                    EditText mEdtNotes = view.findViewById(R.id.mEdtNotes);
                    itemObj.put("notes", mEdtNotes.getText().toString());

                    itemObj.put("image", AppUtil.encodeToBase64(mContext,orderModels.get(i).getImgUri()));
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
            if(ValidationUtil.validateString(s)) {
                addOrder(s);
            }else {
                dialog.dismissProgress(ActivityAddOrder.this);
            }
        }
    }*/

    /*private void addOrder(String data) {
        PrintLog.d(TAG, data);
        Call<ResponseBody> call = APIClient.getInstance().addOrder(mSessionUtil.getApiToken(), data);
        mApiCall.makeApiCall(mContext, false, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                dialog.dismissProgress(ActivityAddOrder.this);
                if (ValidationUtil.validateString(responseData)) {
                    Gson gson = new Gson();
                    CommonRes model = gson.fromJson(responseData, CommonRes.class);
                    if (ValidationUtil.validateString(model.getMessage())) {
                        AppUtil.showToast(mContext, model.getMessage());
                    }

                    if (model.getStatus() == StandardStatusCodes.SUCCESS) {
                        EventBus.getDefault().post(new UpdateOrderList());
                        finish();
                    }
                }
            }

            @Override
            public void failure(String responseData) {
                dialog.dismissProgress(ActivityAddOrder.this);
                if (ValidationUtil.validateString(responseData)) {
                    PrintLog.e(TAG, responseData);
                }
            }
        });
    }*/
}