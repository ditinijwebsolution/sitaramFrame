package com.om.sitaramfrem.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.om.sitaramfrem.activities.ActivityLogin;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class AppUtil {

    Activity mActivity;

    public AppUtil(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void hideSoftKeyboard(int timeout) {
        try {
            if (this.mActivity != null) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (inputMethodManager != null) {
                            inputMethodManager.hideSoftInputFromWindow(mActivity.getWindow().getDecorView().getRootView().getWindowToken(), 2);
                        }
                    }
                }, (long) timeout);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                network = connectivityManager.getActiveNetwork();
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            } else {
                return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
            }
        }
        return false;
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String getVersionName(Context mContext) {
        String mVersionName = "";
        try {
            mVersionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return mVersionName;
    }

    public static String encodeToBase64(Context mContext, Uri imageUri) {
        String base64Str = "";
        if(imageUri!=null) {
            try {
                InputStream imageStream = mContext.getContentResolver().openInputStream(imageUri);
                Bitmap image = BitmapFactory.decodeStream(imageStream);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                base64Str = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return base64Str;
    }

    public static void autoLogout(Activity mActivity) {
        if(mActivity!=null){
            SessionUtil mSessionUtil = new SessionUtil(mActivity);
            mSessionUtil.logOut();
            Intent intent = new Intent(mActivity, ActivityLogin.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mActivity.startActivity(intent);
            mActivity.finish();
        }
    }

    public static void openUrl(Context context,String url){
        if(ValidationUtil.validateString(url)) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        }
    }

    public static String getFileNameFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "default.jpg"; // Handle empty or null URLs
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
