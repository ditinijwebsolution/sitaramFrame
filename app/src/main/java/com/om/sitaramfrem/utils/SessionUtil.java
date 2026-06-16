package com.om.sitaramfrem.utils;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class SessionUtil {

    private SharedPreferences preferences;
    private static final String ISLOGIN = "isLogin";
    private static final String ID = "id";
    private static final String MOBILE_NO = "mobile_no";
    private static final String PASS = "pass";
    private static final String API_TOKEN = "api_token";
    private static final String FCM_TOKEN = "fcm_token";

    public SessionUtil(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setData(String id, String mobile, String password) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ISLOGIN, true);
        editor.putString(ID, id);
        editor.putString(MOBILE_NO, mobile);
        editor.putString(PASS, password);
        editor.apply();
    }

    public boolean isLogin() {
        return preferences.getBoolean(ISLOGIN, false);
    }

    public String getMob() {
        return preferences.getString(MOBILE_NO, "");
    }

    public void setMob(String mob) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MOBILE_NO, mob);
        editor.apply();
    }

    public String getPass() {
        return preferences.getString(PASS, "");
    }

    public void setPass(String pass) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PASS, pass);
        editor.apply();
    }

    public String getID() {
        return preferences.getString(ID, "");
    }

    public void setApiToken(String apiToken) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(API_TOKEN, "Bearer " + apiToken);
        editor.apply();
    }

    public String getApiToken() {
        return preferences.getString(API_TOKEN, "");
    }

    public void setFcmToken(String fcmToken){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(FCM_TOKEN, fcmToken);
        editor.apply();
    }

    public String getFcmToken() {
        return preferences.getString(FCM_TOKEN, "");
    }

    public void logOut() {
        String fcmToken = getFcmToken();
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        setFcmToken(fcmToken);
    }
}
