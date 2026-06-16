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
import com.om.sitaramfrem.activities.ActivityHome;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.FragmentProfileBinding;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.models.profile.ProfileModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class FragmentProfile extends Fragment {
    private FragmentProfileBinding binding;
    private Context mContext;
    private SessionUtil mSessionUtil;
    private ApiCall mApiCall;
    private static final String TAG = FragmentProfile.class.getSimpleName();

    private String name = "";
    private String mobile = "";
    private String address = "";

    public FragmentProfile() {
        // Required empty public constructor
    }

    public static FragmentProfile newInstance() {
        FragmentProfile fragment = new FragmentProfile();
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
        binding = FragmentProfileBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSessionUtil = new SessionUtil(mContext);
        mApiCall = new ApiCall();

        binding.mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isValidForm()){
                    profileUpdate();
                }
            }
        });

        profile();

    }

    private void profile(){
        Call<ResponseBody> call = APIClient.getInstance().profile(mSessionUtil.getApiToken());
        mApiCall.makeApiCall(mContext, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                if(ValidationUtil.validateString(responseData)){
                    Gson gson = new Gson();
                    ProfileModel model = gson.fromJson(responseData,ProfileModel.class);
                    if(model.getStatus() == StandardStatusCodes.SUCCESS){
                        if(model.getData()!=null){
                            if(ValidationUtil.validateString(model.getData().getName())) {
                                binding.mEdtName.setText(model.getData().getName());
                                name = model.getData().getName();
                            }

                            if(ValidationUtil.validateString(model.getData().getMobileNumber())) {
                                binding.mEdtMobile.setText(model.getData().getMobileNumber());
                                mobile = model.getData().getMobileNumber();
                            }

                            if(ValidationUtil.validateString(model.getData().getAddress())) {
                                binding.mEdtAddress.setText(model.getData().getAddress());
                                address = model.getData().getAddress();
                            }
                        }
                    }else if (model.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                        AppUtil.showToast(mContext, model.getMessage());
                        AppUtil.autoLogout(getActivity());
                    }else {
                        AppUtil.showToast(mContext,model.getMessage());
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
    }

    private boolean isValidForm(){
        return ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtName, binding.mTilName, mContext.getString(R.string.err_enter_name), 3, "",-1,"")
                && ValidationUtil.isBlankETAndTextInputError(mContext, binding.mEdtAddress, binding.mTilAddress, mContext.getString(R.string.err_enter_address), 3, "",-1,"");
    }

    private void profileUpdate(){
        try {
            JSONObject object = new JSONObject();
            object.put("name",binding.mEdtName.getText().toString());
            object.put("address",binding.mEdtAddress.getText().toString());

            String data = object.toString();
            Call<ResponseBody> call = APIClient.getInstance().profileUpdate(mSessionUtil.getApiToken(),data);
            mApiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    if(ValidationUtil.validateString(responseData)){
                        Gson gson = new Gson();
                        CommonRes model = gson.fromJson(responseData,CommonRes.class);
                        if(ValidationUtil.validateString(model.getMessage())){
                            AppUtil.showToast(mContext,model.getMessage());
                        }

                        if(model.getStatus() != StandardStatusCodes.SUCCESS){
                            binding.mEdtName.setText(name);
                            binding.mEdtMobile.setText(mobile);
                            binding.mEdtAddress.setText(address);
                        }else if (model.getStatus() == StandardStatusCodes.UNAUTHORISE) {
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