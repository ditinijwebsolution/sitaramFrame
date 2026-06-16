package com.om.sitaramfrem.api;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.CustomDialog;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.ValidationUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiCall {
    private CustomDialog customDialog;
    private String TAG = ApiCall.class.getSimpleName();
    private Dialog dialog;

    public void makeApiCall(final Context context, final boolean isLoadingNeeded, Call<ResponseBody> call, final ApiCallback ApiCallback) {
        customDialog = new CustomDialog();
        if (AppUtil.isConnectingToInternet(context)) {
            //Todo isLoadingNeed
            if (isLoadingNeeded)
                customDialog.displayProgress(context);
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (isLoadingNeeded)
                        customDialog.dismissProgress(context);
                    String bodyString = null;
                    if (response.isSuccessful()) {
                        try {
                            bodyString = new String(response.body().bytes());
                            PrintLog.e(TAG, "WS call success res :=> " + bodyString);
                            ApiCallback.success(bodyString);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            bodyString = new String(response.errorBody().bytes());
                            if (ValidationUtil.validateString(bodyString)) {
                                PrintLog.e(TAG, "WS call success res :=> " + bodyString);
                                ApiCallback.failure(bodyString);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (isLoadingNeeded) {
                        customDialog.dismissProgress(context);
                    }
                    String errorMessage = t.getMessage() != null ? t.getMessage() : "Network request failed";
                    Log.e("LoginAPI", "API Failure: " + errorMessage, t);
                    ApiCallback.failure(errorMessage);
                }
            });
        } else {
            AppUtil.showToast(context, "No Internet Connection");
        }
    }
}