package com.om.sitaramfrem.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.ActivityRegisterBinding;
import com.om.sitaramfrem.models.register.RegisterModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.Constants;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActivityRegister extends BaseActivity implements View.OnClickListener {

    private ActivityRegisterBinding binding;
    private Context mContext;

    private static final String TAG = ActivityRegister.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;

        binding.mIvBack.setOnClickListener(this);
        binding.mBtnRegister.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    private boolean isValidForm(){
        return ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtName, binding.mTilName, mContext.getString(R.string.err_enter_name), 3, "", 120, "")
                && ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtMobile, binding.mTilMobile, mContext.getString(R.string.err_enter_mobile_number), 10, mContext.getString(R.string.err_enter_valid_mobile_number))
                && ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtAddress, binding.mTilAddress, mContext.getString(R.string.err_enter_address), 3, "",-1,"")
                && ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtPassword, binding.mTilPassword, mContext.getString(R.string.err_enter_password), 6, "",60,"")
                && ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtCPassword, binding.mTilCPassword, mContext.getString(R.string.err_enter_confirm_password), 6, "",60,"")
                && ValidationUtil.isPasswordSameETAndTextInputError(mContext, binding.mEdtPassword, binding.mEdtCPassword, binding.mTilCPassword, mContext.getString(R.string.err_new_confirm_password_not_match));
    }

    @Override
    public void onClick(View view) {
        if(view == binding.mIvBack){
            onBackPressed();
        }else if(view == binding.mBtnRegister){
            if(isValidForm()){
                registration();
            }
        }
    }

    private void registration(){
        try {
            JSONObject object = new JSONObject();
            object.put("name",binding.mEdtName.getText().toString());
            object.put("mobile_number",binding.mEdtMobile.getText().toString());
            object.put("address",binding.mEdtAddress.getText().toString());
            object.put("password",binding.mEdtPassword.getText().toString());
            object.put("confirm_password",binding.mEdtCPassword.getText().toString());

            String data = object.toString();
            Call<ResponseBody> call = APIClient.getInstance().registration(data);
            ApiCall mApiCall = new ApiCall();
            mApiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    if(ValidationUtil.validateString(responseData)){
                        Gson gson = new Gson();
                        RegisterModel model = gson.fromJson(responseData,RegisterModel.class);
                        if(ValidationUtil.validateString(model.getMessage())){
                            AppUtil.showToast(mContext,model.getMessage());
                        }

                        if(model.getStatus() == StandardStatusCodes.SUCCESS){
                            Intent intent = new Intent(mContext,ActivityOTP.class);
                            intent.putExtra(ActivityOTP.MOBILE_NUMBER, binding.mEdtMobile.getText().toString());
                            intent.putExtra(ActivityOTP.SCREEN_TYPE, Constants.REGISTER_SCREEN);
                            intent.putExtra(ActivityOTP.OTP,model.getData().getOtp());
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void failure(String responseData) {
                    if(ValidationUtil.validateString(responseData)){
                        PrintLog.e(TAG,responseData);
                    }
                }
            });
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}