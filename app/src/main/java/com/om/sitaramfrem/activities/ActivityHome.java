package com.om.sitaramfrem.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.gson.Gson;
import com.om.sitaramfrem.dialogs.DialogAppUpdate;
import com.om.sitaramfrem.fragments.FragmentChangePassword;
import com.om.sitaramfrem.fragments.FragmentHome;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.ActivityHomeBinding;
import com.om.sitaramfrem.fragments.FragmentHomeNew;
import com.om.sitaramfrem.fragments.FragmentInActiveCategory;
import com.om.sitaramfrem.fragments.FragmentProfile;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.CustomDialog;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActivityHome extends BaseActivity implements View.OnClickListener {
    private ActivityHomeBinding binding;
    private Context mContext;

    private FragmentManager mFragmentManager;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private SessionUtil mSessionUtil;
    private static final String TAG = ActivityHome.class.getSimpleName();

    private AppUpdateManager mAppUpdateManager;
    private static final int RC_APP_UPDATE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;

       /* ViewCompat.setOnApplyWindowInsetsListener(binding.mDummyView, (v, insets) -> {
            Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());

           *//* // Apply top padding equal to status bar height
            v.setPadding(
                    v.getPaddingLeft(),
                    statusBarInsets.top,
                    v.getPaddingRight(),
                    v.getPaddingBottom()
            );*//*

            binding.mDummyView.getLayoutParams().height = statusBarInsets.top;

            return insets;
        });*/

        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        /*mAppUpdateManager.registerListener();*/
        checkUpdate();
        mDrawerLayout = binding.mDrawerLayout;
        mToolbar = binding.toolbar;
        setSupportActionBar(mToolbar);

        mSessionUtil = new SessionUtil(mContext);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                binding.mIvMenu.setImageResource(R.drawable.ic_arrow_back);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                binding.mIvMenu.setImageResource(R.drawable.ic_menu);
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        //init
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().add(binding.mAppBarMain.mFrmContainer.getId(), FragmentHomeNew.newInstance()).commit();
        binding.mTxtHome.setSelected(true);
        binding.mTxtToolbarTitle.setText(mContext.getString(R.string.lbl_home));

        binding.mIvMenu.setOnClickListener(this);
        binding.mIvCart.setOnClickListener(this);
        binding.mTxtHome.setOnClickListener(this);
        binding.mTxtOrders.setOnClickListener(this);
        binding.mTxtInActive.setOnClickListener(this);
        binding.mTxtMenuProfile.setOnClickListener(this);
        binding.mTxtMenuChangePassword.setOnClickListener(this);
        binding.mTxtMenuLogout.setOnClickListener(this);

        showNotificationPermissionPrompt();
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == binding.mIvMenu) {
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            } else {
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        } else if (view == binding.mIvCart) {
            Intent intent = new Intent(ActivityHome.this, ActivityCartList.class);
            startActivity(intent);
        } else if (view == binding.mTxtHome) {
            if (!binding.mTxtHome.isSelected()) {
                binding.mTxtHome.setSelected(true);
                binding.mTxtOrders.setSelected(false);
                binding.mTxtInActive.setSelected(false);
                binding.mTxtMenuProfile.setSelected(false);
                binding.mTxtMenuChangePassword.setSelected(false);
                binding.mTxtToolbarTitle.setText(mContext.getString(R.string.lbl_home));
                changeFragment(FragmentHomeNew.newInstance());
            }
        } else if (view == binding.mTxtOrders) {
            if (!binding.mTxtOrders.isSelected()) {
                binding.mTxtOrders.setSelected(true);
                binding.mTxtHome.setSelected(false);
                binding.mTxtInActive.setSelected(false);
                binding.mTxtMenuProfile.setSelected(false);
                binding.mTxtMenuChangePassword.setSelected(false);
                binding.mTxtToolbarTitle.setText(mContext.getString(R.string.lbl_orders));
                changeFragment(FragmentHome.newInstance());
            }
        } else if (view == binding.mTxtInActive) {
            if (!binding.mTxtInActive.isSelected()) {
                binding.mTxtInActive.setSelected(true);
                binding.mTxtHome.setSelected(false);
                binding.mTxtOrders.setSelected(false);
                binding.mTxtMenuProfile.setSelected(false);
                binding.mTxtMenuChangePassword.setSelected(false);
                binding.mTxtToolbarTitle.setText(mContext.getString(R.string.lbl_out_of_stock_frame));
                changeFragment(FragmentInActiveCategory.newInstance());
            }
        } else if (view == binding.mTxtMenuProfile) {
            if (!binding.mTxtMenuProfile.isSelected()) {
                binding.mTxtMenuProfile.setSelected(true);
                binding.mTxtHome.setSelected(false);
                binding.mTxtOrders.setSelected(false);
                binding.mTxtInActive.setSelected(false);
                binding.mTxtMenuChangePassword.setSelected(false);
                binding.mTxtToolbarTitle.setText(mContext.getString(R.string.lbl_profile));
                changeFragment(FragmentProfile.newInstance());
            }
        } else if (view == binding.mTxtMenuChangePassword) {
            if (!binding.mTxtMenuChangePassword.isSelected()) {
                binding.mTxtMenuChangePassword.setSelected(true);
                binding.mTxtHome.setSelected(false);
                binding.mTxtOrders.setSelected(false);
                binding.mTxtInActive.setSelected(false);
                binding.mTxtMenuProfile.setSelected(false);
                binding.mTxtToolbarTitle.setText(mContext.getString(R.string.lbl_change_password));
                changeFragment(FragmentChangePassword.newInstance());
            }
        } else if (view == binding.mTxtMenuLogout) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    logOut();
                }
            }, 350);
        }
    }

    private void changeFragment(Fragment mFragment) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (mFragment != null) {
            //isTap = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFragmentManager.beginTransaction().replace(binding.mAppBarMain.mFrmContainer.getId(), mFragment).commit();
                }
            }, 250);
        }
    }

    private void logOut() {
        CustomDialog customDialog = new CustomDialog();
        customDialog.showDialogTwoButton(mContext, getString(R.string.lbl_logout), mContext.getString(R.string.msg_are_you_sure_logout),
                getString(R.string.lbl_logout), getString(R.string.lbl_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        logoutApi();

                    }
                }, null);
    }

    private void logoutApi() {
        Call<ResponseBody> call = APIClient.getInstance().logout(mSessionUtil.getApiToken());
        ApiCall apiCall = new ApiCall();
        apiCall.makeApiCall(mContext, true, call, new ApiCallback() {
            @Override
            public void success(String responseData) {
                if (ValidationUtil.validateString(responseData)) {
                    Gson gson = new Gson();
                    CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                    if (commonRes.getStatus() == StandardStatusCodes.SUCCESS) {
                        mSessionUtil.logOut();
                        Intent intent = new Intent(mContext, ActivityLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else if (commonRes.getStatus() == StandardStatusCodes.UNAUTHORISE) {
                        AppUtil.showToast(mContext, commonRes.getMessage());
                        AppUtil.autoLogout(ActivityHome.this);
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

    @Override
    protected void onResume() {
        super.onResume();
        //checkversion();
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
                {
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            activityResultLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    );
                }
            }
        });
    }

    private void checkversion() {
        try {
            JSONObject object = new JSONObject();
            object.put("version", AppUtil.getVersionName(mContext));
            String strJson = object.toString();
            PrintLog.d(TAG, strJson);
            Call<ResponseBody> call = APIClient.getInstance().checkversion(strJson);
            ApiCall mApiCall = new ApiCall();
            mApiCall.makeApiCall(mContext, true, call, new ApiCallback() {
                @Override
                public void success(String responseData) {
                    if (ValidationUtil.validateString(responseData)) {
                        Gson gson = new Gson();
                        CommonRes commonRes = gson.fromJson(responseData, CommonRes.class);
                        if (commonRes.getStatus() == StandardStatusCodes.NO_DATA_FOUND) {
                            showUpdatePopup();
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

    private void checkUpdate(){
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ){
                    mAppUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            activityResultLauncher,
                            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    );
                }
            }
        });
        mAppUpdateManager.getAppUpdateInfo().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "Exception " + e.getMessage());
            }
        });
    }

    private void showUpdatePopup() {
        DialogAppUpdate mDialogAppUpdate = DialogAppUpdate.newInstance();
        mDialogAppUpdate.setCancelable(false);
        mDialogAppUpdate.setOnDialogListener(new DialogAppUpdate.OnDialogListener() {
            @Override
            public void onDialogItemClick(String action) {
                if (ValidationUtil.validateString(action)) {
                    if (action.equals("update")) {
                        mDialogAppUpdate.dismiss();
                        AppUtil.openUrl(mContext, mContext.getString(R.string.playstore_link)+mContext.getPackageName());
                    }
                }
            }
        });
        mDialogAppUpdate.show(getSupportFragmentManager(), DialogAppUpdate.TAG);
    }

    private void showNotificationPermissionPrompt(){
        if(Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
            }
        }
    }

    private ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {

    });
}