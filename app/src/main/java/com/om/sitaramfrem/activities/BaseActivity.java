package com.om.sitaramfrem.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import android.content.Context;
import android.graphics.Insets;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.om.sitaramfrem.R;
import com.om.sitaramfrem.utils.AppUtil;

public class BaseActivity extends AppCompatActivity {

    public AppUtil mAppUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
            window.getDecorView().setOnApplyWindowInsetsListener((view, windowInsets) -> {
                Insets statusBarInsets = windowInsets.getInsets(WindowInsets.Type.statusBars());
                Insets navigationInsets = windowInsets.getInsets(WindowInsets.Type.navigationBars());
                //view.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
                view.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_root_view));
                view.setPadding(0, statusBarInsets.top, 0, navigationInsets.bottom);
                return windowInsets;
            });
        } else {
            // For Android 14 and below
            window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        }
        mAppUtil = new AppUtil(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            /**
             * It gets into the above IF-BLOCK if anywhere the screen is touched.
             */

            View v = getCurrentFocus();
            if (v instanceof EditText) {


                /**
                 * Now, it gets into the above IF-BLOCK if an EditText is already in focus, and you tap somewhere else
                 * to take the focus away from that particular EditText. It could have 2 cases after tapping:
                 * 1. No EditText has focus
                 * 2. Focus is just shifted to the other EditText
                 */

                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}