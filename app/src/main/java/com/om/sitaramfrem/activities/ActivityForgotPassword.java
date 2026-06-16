package com.om.sitaramfrem.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import com.om.sitaramfrem.databinding.ActivityForgotPasswordBinding;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.models.register.RegisterModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.Constants;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActivityForgotPassword extends BaseActivity implements View.OnClickListener {

    private ActivityForgotPasswordBinding binding;
    private Context mContext;

    private static final String TAG = ActivityForgotPassword.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;

        binding.mIvBack.setOnClickListener(this);
        binding.mBtnSubmit.setOnClickListener(this);
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
        }else if(view == binding.mBtnSubmit){
            if(isValidForm()){
                forgotPassword();
            }
        }
    }

    private boolean isValidForm(){
        return ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtMobileNumber, binding.mTilMobileNumber, mContext.getString(R.string.err_enter_mobile_number), 10, mContext.getString(R.string.err_enter_valid_mobile_number));
    }

    private void forgotPassword(){
        try {
            JSONObject object = new JSONObject();
            object.put("mobile_number",binding.mEdtMobileNumber.getText().toString());

            String data = object.toString();
            Call<ResponseBody> call = APIClient.getInstance().forgotPassword(data);
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
                            intent.putExtra(ActivityOTP.MOBILE_NUMBER, binding.mEdtMobileNumber.getText().toString());
                            intent.putExtra(ActivityOTP.SCREEN_TYPE, Constants.FORGOT_SCREEN);
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