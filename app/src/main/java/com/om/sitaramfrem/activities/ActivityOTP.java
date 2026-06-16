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
import com.om.sitaramfrem.databinding.ActivityOtpBinding;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.Constants;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.GenericSignatureFormatError;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActivityOTP extends BaseActivity implements View.OnClickListener {
    private ActivityOtpBinding binding;
    private Context mContext;

    private ApiCall mApiCall;
    private String strScreen = "";
    private String strOtp = "";
    private String strMobileNumber = "";

    public static final String SCREEN_TYPE = "screenType";
    public static final String OTP = "otp";
    public static final String MOBILE_NUMBER = "mobileNumber";

    private static final String TAG = ActivityOTP.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;

        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            finish();
            return;
        }
        strScreen = bundle.getString(SCREEN_TYPE,"");
        strOtp = bundle.getString(OTP,"");
        strMobileNumber = bundle.getString(MOBILE_NUMBER,"");

        binding.mEdtOTP.setText(strOtp);

        mApiCall = new ApiCall();

        binding.mIvBack.setOnClickListener(this);
        binding.mBtnVerify.setOnClickListener(this);
    }

    private boolean isRegisterScreen(){
        return strScreen.equals(Constants.REGISTER_SCREEN);
    }

    private boolean isForgotScreen(){
        return strScreen.equals(Constants.FORGOT_SCREEN);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onClick(View view) {
        if(view == binding.mIvBack){
            onBackPressed();
        }else if(view == binding.mBtnVerify){
            if(isValidForm()){
                if(isRegisterScreen()){
                    registrationOtp();
                }else if(isForgotScreen()){
                    verifyOtp();
                }
            }
        }
    }

    private boolean isValidForm(){
        return ValidationUtil.isBlankETAndTextInputError(mContext,binding.mEdtOTP,binding.mTilOTP,mContext.getString(R.string.err_enter_otp));
    }

    private void registrationOtp(){
        try {
            JSONObject object = new JSONObject();
            object.put("mobile_number",strMobileNumber);
            object.put("otp",binding.mEdtOTP.getText().toString());

            String data = object.toString();
            PrintLog.d(TAG,data);

            Call<ResponseBody> call = APIClient.getInstance().registrationOtp(data);
            mApiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    if(ValidationUtil.validateString(responseData)){
                        Gson gson = new Gson();
                        CommonRes model = gson.fromJson(responseData,CommonRes.class);
                        if(ValidationUtil.validateString(model.getMessage())){
                            AppUtil.showToast(mContext,model.getMessage());
                        }

                        if(model.getStatus() == StandardStatusCodes.SUCCESS){
                            Intent intent = new Intent(mContext, ActivityLogin.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
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


    private void verifyOtp(){
        try {
            JSONObject object = new JSONObject();
            object.put("mobile_number",strMobileNumber);
            object.put("otp",binding.mEdtOTP.getText().toString());

            String data = object.toString();
            PrintLog.d(TAG,data);

            Call<ResponseBody> call = APIClient.getInstance().verifyOtp(data);
            mApiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    if(ValidationUtil.validateString(responseData)){
                        Gson gson = new Gson();
                        CommonRes model = gson.fromJson(responseData,CommonRes.class);
                        if(ValidationUtil.validateString(model.getMessage())){
                            AppUtil.showToast(mContext,model.getMessage());
                        }

                        if(model.getStatus() == StandardStatusCodes.SUCCESS){
                            Intent intent = new Intent(mContext, ActivityResetPassword.class);
                            intent.putExtra(ActivityResetPassword.MOBILE_NUMBER,strMobileNumber);
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