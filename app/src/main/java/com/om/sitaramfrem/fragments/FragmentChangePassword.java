package com.om.sitaramfrem.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.FragmentChangePasswordBinding;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class FragmentChangePassword extends Fragment {
    private FragmentChangePasswordBinding binding;
    private Context mContext;
    private SessionUtil mSessionUtil;

    private static final String TAG = FragmentChangePassword.class.getSimpleName();

    public FragmentChangePassword() {
        // Required empty public constructor
    }


    public static FragmentChangePassword newInstance() {
        FragmentChangePassword fragment = new FragmentChangePassword();
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
        binding = FragmentChangePasswordBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSessionUtil = new SessionUtil(mContext);

        binding.mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidForm()){
                    changePassword();
                }
            }
        });
    }

    private boolean isValidForm(){
        return ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtPassword, binding.mTilPassword, mContext.getString(R.string.err_enter_current_password), 6, "",60,"")
                && ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtNewPassword, binding.mTilNewPassword, mContext.getString(R.string.err_enter_new_password), 6, "",60,"")
                && ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtCPassword, binding.mTilCPassword, mContext.getString(R.string.err_enter_confirm_password), 6, "",60,"")
                && ValidationUtil.isPasswordSameETAndTextInputError(mContext, binding.mEdtNewPassword, binding.mEdtCPassword, binding.mTilCPassword, mContext.getString(R.string.err_new_confirm_password_not_match));
    }

    private void changePassword(){
        try {
            JSONObject object = new JSONObject();
            object.put("current_password",binding.mEdtPassword.getText().toString());
            object.put("password",binding.mEdtNewPassword.getText().toString());
            object.put("confirm_password",binding.mEdtCPassword.getText().toString());

            String data = object.toString();
            Call<ResponseBody> call = APIClient.getInstance().changePassword(mSessionUtil.getApiToken(),data);
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
                            mSessionUtil.setPass(binding.mEdtNewPassword.getText().toString());

                            binding.mEdtPassword.setText("");
                            binding.mEdtNewPassword.setText("");
                            binding.mEdtCPassword.setText("");
                        }else if(model.getStatus() == StandardStatusCodes.UNAUTHORISE){
                            AppUtil.autoLogout(getActivity());
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

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}