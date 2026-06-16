package com.om.sitaramfrem.utils;

import com.om.sitaramfrem.BuildConfig;

public class PrintLog {

    public static void d(String PageTag, String ExceptionMsg) {
        if (BuildConfig.ISDEBUG) {
            android.util.Log.d("::" + PageTag, "::" + ExceptionMsg);
        }
    }

    public static void e(String PageTag, String ExceptionMsg) {
        if (BuildConfig.ISDEBUG) {
            android.util.Log.e("::" + PageTag, "::" + ExceptionMsg);
        }
    }

    public static void v(String PageTag, String ExceptionMsg) {
        if (BuildConfig.ISDEBUG) {
            android.util.Log.v("::" + PageTag, "::" + ExceptionMsg);
        }
    }

    public static void i(String PageTag, String ExceptionMsg) {
        if (BuildConfig.ISDEBUG) {
            android.util.Log.i("::" + PageTag, "::" + ExceptionMsg);
        }
    }

    public static void w(String PageTag, String ExceptionMsg) {
        if (BuildConfig.ISDEBUG) {
            android.util.Log.w("::" + PageTag, "::" + ExceptionMsg);
        }
    }

}
