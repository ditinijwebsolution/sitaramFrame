package com.om.sitaramfrem.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;

import com.om.sitaramfrem.R;

import java.lang.ref.WeakReference;


public class CustomDialog {

    private AlertDialog alertListDialog;
    private Dialog mCustomProgressDialog;
    private AlertDialog oneButtonDialog;
    private AlertDialog twoButtonDialog;
    private WeakReference<Activity> weakActivity;


    public void displayProgress(Context context) {
        this.weakActivity = new WeakReference<>((Activity) context);
        if (this.weakActivity.get() != null && !((Activity) this.weakActivity.get()).isFinishing()) {
            try {
                if (this.mCustomProgressDialog == null) {
                    this.mCustomProgressDialog = new Dialog(context);
                    this.mCustomProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    this.mCustomProgressDialog.setContentView(R.layout.inflate_loading_layout);
                    
                    if (this.mCustomProgressDialog.getWindow() != null) {
                        this.mCustomProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    }
                } else if (this.mCustomProgressDialog.isShowing()) {
                    this.mCustomProgressDialog.dismiss();
                }
                this.mCustomProgressDialog.setCancelable(false);
                if (!((Activity) context).isFinishing()) {
                    this.mCustomProgressDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void dismissProgress(Context context) {

        this.weakActivity = new WeakReference<>((Activity) context);
        if (this.weakActivity.get() != null && !((Activity) this.weakActivity.get()).isFinishing()) {
            Dialog dialog = this.mCustomProgressDialog;
            if (dialog != null && dialog.isShowing()) {
                this.mCustomProgressDialog.dismiss();
            }
        }

    }

    public void showDialogTwoButton(Context context, String title, String message,
                                    String btnPositiveLabel, String btnNegativeLabel,
                                    DialogInterface.OnClickListener positiveButtonClickListener, DialogInterface.OnClickListener negativeButtonClickListener) {


        this.weakActivity = new WeakReference<>((Activity) context);
        if (this.weakActivity.get() != null && !((Activity) this.weakActivity.get()).isFinishing()) {
            AlertDialog alertDialog = this.twoButtonDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            AlertDialog.Builder twoButtonDialogBuilder = new AlertDialog.Builder(context);
            if (ValidationUtil.validateString(title)) {
                twoButtonDialogBuilder.setTitle((CharSequence) title);
            }
            twoButtonDialogBuilder.setMessage((CharSequence) message);
            twoButtonDialogBuilder.setCancelable(false);
            if (positiveButtonClickListener == null) {
                twoButtonDialogBuilder.setPositiveButton((CharSequence) btnPositiveLabel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            } else {
                twoButtonDialogBuilder.setPositiveButton((CharSequence) btnPositiveLabel, positiveButtonClickListener);
            }
            if (negativeButtonClickListener == null) {
                twoButtonDialogBuilder.setNegativeButton((CharSequence) btnNegativeLabel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            } else {
                twoButtonDialogBuilder.setNegativeButton((CharSequence) btnNegativeLabel, negativeButtonClickListener);
            }
            this.twoButtonDialog = twoButtonDialogBuilder.create();
            this.twoButtonDialog.show();
        }
    }


    public void showDialogOneButton(Context context, String title, String message, String btnPositiveLabel, DialogInterface.OnClickListener positiveButtonClickListener) {
        this.weakActivity = new WeakReference<>((Activity) context);
        if (this.weakActivity.get() != null && !((Activity) this.weakActivity.get()).isFinishing()) {
            AlertDialog alertDialog = this.oneButtonDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
            }
            AlertDialog.Builder oneButtonDialogBuilder = new AlertDialog.Builder(context);
            if (ValidationUtil.validateString(title)) {
                oneButtonDialogBuilder.setTitle((CharSequence) title);
            }
            oneButtonDialogBuilder.setMessage((CharSequence) message);
            oneButtonDialogBuilder.setCancelable(false);
            if (positiveButtonClickListener == null) {
                oneButtonDialogBuilder.setPositiveButton((CharSequence) btnPositiveLabel, (DialogInterface.OnClickListener) new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
            } else {
                oneButtonDialogBuilder.setPositiveButton((CharSequence) btnPositiveLabel, positiveButtonClickListener);
            }
            this.oneButtonDialog = oneButtonDialogBuilder.create();
            this.oneButtonDialog.show();
        }
    }


}
