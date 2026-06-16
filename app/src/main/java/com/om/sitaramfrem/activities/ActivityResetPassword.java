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
import com.om.sitaramfrem.databinding.ActivityResetPasswordBinding;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActivityResetPassword extends BaseActivity implements View.OnClickListener {
    private ActivityResetPasswordBinding binding;
    private Context mContext;
    private String strMobileNumber = "";

    public static final String MOBILE_NUMBER = "mobileNumber";
    private static final String TAG = ActivityResetPassword.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        if(bundle == null){
            finish();
            return;
        }
        strMobileNumber = bundle.getString(MOBILE_NUMBER,"");

        binding.mIvBack.setOnClickListener(this);
        binding.mBtnUpdate.setOnClickListener(this);
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
        }else if(view == binding.mBtnUpdate){
            if(isValidForm()){
                resetPassword();
            }
        }
    }

    private boolean isValidForm(){
        return ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtPassword, binding.mTilPassword, mContext.getString(R.string.err_enter_new_password), 6, "",60,"")
                && ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtCPassword, binding.mTilCPassword, mContext.getString(R.string.err_enter_confirm_password), 6, "",60,"")
                && ValidationUtil.isPasswordSameETAndTextInputError(mContext, binding.mEdtPassword, binding.mEdtCPassword, binding.mTilCPassword, mContext.getString(R.string.err_new_confirm_password_not_match));
    }

    private void resetPassword(){
        try {
            JSONObject object = new JSONObject();
            object.put("mobile_number",strMobileNumber);
            object.put("password",binding.mEdtPassword.getText().toString());
            object.put("confirm_password",binding.mEdtCPassword.getText().toString());

            String data = object.toString();
            PrintLog.d(TAG,data);
            Call<ResponseBody> call = APIClient.getInstance().resetPassword(data);
            ApiCall mApiCall = new ApiCall();
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
}