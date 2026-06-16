package com.om.sitaramfrem.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.ApiConstant;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.controller.FCMController;
import com.om.sitaramfrem.databinding.ActivityLoginBinding;
import com.om.sitaramfrem.models.login.LoginModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActivityLogin extends BaseActivity implements View.OnClickListener {

    private FCMController mFCMController;
    private ActivityLoginBinding binding;
    private Context mContext;
    private SessionUtil mSessionUtil;
    private static String TAG = ActivityLogin.class.getSimpleName();
    private static final String LOGIN_LOG_TAG = "LoginAPI";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        if (getWindow() != null) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        mContext = this;
        mSessionUtil = new SessionUtil(mContext);
        mFCMController = new FCMController(this);
        mFCMController.generateFCMToken();

        binding.mTxtCreateAccount.setOnClickListener(this);
        binding.mBtnLogin.setOnClickListener(this);
        binding.mTxtForgotPassword.setOnClickListener(this);

        if(mSessionUtil.isLogin()){
            loginCall(mSessionUtil.getMob(),mSessionUtil.getPass());
        }
    }

    @Override
    public void onClick(View view) {
        if(view == binding.mTxtCreateAccount){
            startActivity(new Intent(mContext,ActivityRegister.class));
        }else if(view == binding.mBtnLogin){
            if(isValidForm()){
                loginCall(binding.mEdtMobile.getText().toString(),binding.mEdtPassword.getText().toString());
            }
        }else if(view == binding.mTxtForgotPassword){
            startActivity(new Intent(mContext,ActivityForgotPassword.class));
        }
    }

    private boolean isValidForm(){
        return ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtMobile, binding.mTilMobile, mContext.getString(R.string.err_enter_mobile_number), 10, mContext.getString(R.string.err_enter_valid_mobile_number))
                && ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtPassword, binding.mTilPassword, mContext.getString(R.string.err_enter_password), 6, "",60,"");
    }

    private void loginCall(String mob,String pass){
        PrintLog.e("DeviceToken","Token :"+mSessionUtil.getFcmToken());
        try {
            JSONObject object = new JSONObject();
            object.put("mobile_number", mob);
            object.put("password", pass);
            object.put("fcm_token", mSessionUtil.getFcmToken());
            PrintLog.e(TAG, "Login Request URL: " + ApiConstant.BASE_URL + "login");
            PrintLog.e(TAG, "Login Request Data: " + object.toString());
            Log.e(LOGIN_LOG_TAG, "Login Request URL: " + ApiConstant.BASE_URL + "login");
            Log.e(LOGIN_LOG_TAG, "Login Request Data: " + object.toString());
            Call<ResponseBody> call = APIClient.getInstance().login(object.toString());
            ApiCall apiCall = new ApiCall();
            apiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    PrintLog.e(TAG, "Login Response Data: " + responseData);
                    Log.e(LOGIN_LOG_TAG, "Login Response Data: " + responseData);
                    if (ValidationUtil.validateString(responseData)) {
                        Gson gson = new Gson();
                        LoginModel loginModel = gson.fromJson(responseData, LoginModel.class);
                        if (loginModel.getStatus() == StandardStatusCodes.SUCCESS
                                && loginModel.getData() != null) {
                            mSessionUtil.setData(String.valueOf(loginModel.getData().getId()),
                                    mob, pass);
                            mSessionUtil.setApiToken(loginModel.getData().getToken());

                            startActivity(new Intent(mContext,ActivityHome.class));
                            finish();
                        } else {
                            mSessionUtil.logOut();
                            AppUtil.showToast(mContext, loginModel.getMessage());
                        }
                    }
                }

                @Override
                public void failure(String responseData) {
                    mSessionUtil.logOut();
                    PrintLog.e(TAG, responseData);
                    Log.e(LOGIN_LOG_TAG, "Login Failure: " + responseData);
                    AppUtil.showToast(mContext, ValidationUtil.validateString(responseData)
                            ? responseData
                            : "Login failed. Please try again.");
                }
            });
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}