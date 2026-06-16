package com.om.sitaramfrem.utils;

import android.content.Context;
import android.util.Patterns;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.om.sitaramfrem.R;

import java.util.regex.Pattern;

public class ValidationUtil {
    public static String getValidEmailKey(String email) {
        return email.replaceAll("\\.", "-DOT-").replaceAll("@", "-AT-");
    }

    public static Pattern getAlphabetPattern() {
        return Pattern.compile("^[_a-zA-Z0-9 /-]+$");
    }

    public static Pattern getAlphaMixPattern() {
        return Pattern.compile("^[_a-zA-Z0-9 /-/./`/'\"/;/:/,]+$");
    }

    public static Pattern getEmailPattern() {
        return Pattern.compile("^[_A-Za-z0-9/.]+([_A-Za-z0-9-/+/-/?/*/=///^/!/#/$/%/'/`/{/}/|/~/;]+)*@[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*(\\.[A-Za-z]{2,})$");
    }

    public static Pattern getPhonePattern() {
        return Patterns.PHONE;
    }

    public static Pattern getNumericPattern() {
        return Pattern.compile("^[0-9]");
    }

    public static Pattern getMobileNoPattern() {
        return Pattern.compile("^[0-9]{10}");
    }

    public static boolean validateInputWithPattern(Pattern mPattern, String mStrValue) {
        return mPattern.matcher(mStrValue).matches();
    }

    public static boolean validateString(String value) {
        return value != null && !value.trim().isEmpty() && !value.trim().toLowerCase().equalsIgnoreCase("null");
    }

    public static String convertFirstCharCapital(String value) {
        String result = "";
        if (validateString(value)) {
            String str = " ";
            String[] mStringWord = value.split(str);
            for (int i = 0; i < mStringWord.length; i++) {
                if (mStringWord[i].length() > 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(result);
                    sb.append(mStringWord[i].substring(0, 1).toUpperCase());
                    sb.append(mStringWord[i].substring(1).toLowerCase());
                    sb.append(str);
                    result = sb.toString();
                }
            }
        }
        return result.trim();
    }

    public static String firstCharFromString(String value) {
        String result = "";

        if (validateString(value)) {
            String[] mStringWord = value.split(" ");

            for (int i = 0; i < mStringWord.length; i++) {
                if (mStringWord[i].length() > 0) {
                    result += mStringWord[i].substring(0, 1).toUpperCase();
                }
            }
        }

        return result.trim();
    }

    public static boolean isValidEmailETErr(Context context, String msg, EditText editText) {
        /*if (!(editText.getText().toString().trim().toLowerCase()
                .matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))) {
            editText.setError(msg);
            editText.requestFocus();
            return false;
        } else {
            return true;
        }*/

        if (isEmailValid(editText.getText().toString().trim().toLowerCase()) == false) {
            editText.setError(msg);
            editText.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidEmailETAndTextInputErr(Context context, String msg, EditText editText, TextInputLayout textInputLayout) {
        /*if (!(editText.getText().toString().trim().toLowerCase()
                .matches("[a-zA-Z0-9._-]+@[a-z]+\.+[a-z]+"))) {
            textInputLayout.setError(msg);
            editText.requestFocus();
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }*/

        if (isEmailValid(editText.getText().toString().trim().toLowerCase()) == false) {
            textInputLayout.setError(msg);
            editText.requestFocus();
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }

    }

    public static boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordSame(Context context, EditText edtPassword1, EditText edtPassword2, String msg, int snackbarPosition) {
        String strPassword1 = edtPassword1.getText().toString().trim();
        String strPassword2 = edtPassword2.getText().toString().trim();
        if (!strPassword1.equals(strPassword2)) {
            edtPassword2.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPasswordSameETError(Context context, EditText edtPassword1, EditText edtPassword2, String msg) {
        String strPassword1 = edtPassword1.getText().toString().trim();
        String strPassword2 = edtPassword2.getText().toString().trim();
        if (!strPassword1.equals(strPassword2)) {
            edtPassword2.setError(msg);
            edtPassword2.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public static boolean isPasswordSameETAndTextInputError(Context context, EditText edtPassword1, EditText edtPassword2, TextInputLayout tilPassword2, String msg) {
        String strPassword1 = edtPassword1.getText().toString().trim();
        String strPassword2 = edtPassword2.getText().toString().trim();
        if (!strPassword1.equals(strPassword2)) {
            tilPassword2.setError(msg);
            edtPassword2.requestFocus();
            return false;
        } else {
            tilPassword2.setErrorEnabled(false);
            return true;
        }
    }

    public static boolean isBlankETError(Context context, EditText editText, String msg, int minVal, int maxVal) {
        String strEditTextVal = editText.getText().toString().trim();
        if (strEditTextVal.length() == 0) {
            editText.setError(msg);
            editText.requestFocus();
            return false;
        } else if (minVal != -1 && strEditTextVal.length() < minVal) {
            editText.setError(context.getString(R.string.err_min_text) + " " + minVal + " " + context.getString(R.string.err_character_required));
            editText.requestFocus();
            return false;
        } else if (maxVal != -1 && strEditTextVal.length() > maxVal) {
            editText.setError(context.getString(R.string.err_max_text) + " " + maxVal + " " + context.getString(R.string.err_character_required));
            editText.requestFocus();
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    // Check Empty data
    public static boolean isBlankETAndTextInputError(Context context, EditText editText, TextInputLayout textInputLayout, String msg) {
        String strEditTextVal = editText.getText().toString().trim();
        if (strEditTextVal.length() == 0) {
            textInputLayout.setError(msg);
            editText.requestFocus();
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    // Check Empty data with minimum and maximum characters validation
    public static boolean isBlankETAndTextInputError(Context context, EditText editText, TextInputLayout textInputLayout, String msg, int minVal, String minErrorMsg, int maxVal, String maxErrorMsg) {
        String strEditTextVal = editText.getText().toString().trim();
        if (strEditTextVal.length() == 0) {
            textInputLayout.setError(msg);
            editText.requestFocus();
            return false;
        } else if (minVal != -1 && strEditTextVal.length() < minVal) {
            if (validateString(minErrorMsg)) {
                textInputLayout.setError(minErrorMsg);
            } else {
                textInputLayout.setError(context.getString(R.string.err_min_text) + " " + minVal + " " + context.getString(R.string.err_character_required));
            }
            editText.requestFocus();
            return false;
        } else if (maxVal != -1 && strEditTextVal.length() > maxVal) {
            if (validateString(maxErrorMsg)) {
                textInputLayout.setError(maxErrorMsg);
            }else{
                textInputLayout.setError(context.getString(R.string.err_max_text) + " " + maxVal + " " + context.getString(R.string.err_character_required));
            }
            editText.requestFocus();
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    // Check Empty data with fix characters validation
    public static boolean isBlankETAndTextInputError(Context context, EditText editText, TextInputLayout textInputLayout, String msg, int fixVal, String fixErrorMsg) {
        String strEditTextVal = editText.getText().toString().trim();
        if (strEditTextVal.length() == 0) {
            textInputLayout.setError(msg);
            editText.requestFocus();
            return false;
        } else if (fixVal != -1 && strEditTextVal.length() != fixVal) {
            if (validateString(fixErrorMsg)) {
                textInputLayout.setError(fixErrorMsg);
            } else {
                textInputLayout.setError(fixVal + " " + context.getString(R.string.err_character_required));
            }
            editText.requestFocus();
            return false;
        }  else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

}
