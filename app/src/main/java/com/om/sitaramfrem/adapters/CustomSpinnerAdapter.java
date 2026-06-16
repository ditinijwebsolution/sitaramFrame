package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;


import com.om.sitaramfrem.R;
import com.om.sitaramfrem.utils.ValidationUtil;

import java.util.ArrayList;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private String mSelectedOption = "";
    Context mContext;
    ArrayList<String> mListData = new ArrayList<>();
    private boolean isHintShow = false;

    public CustomSpinnerAdapter(Context context, int textViewResourceId, ArrayList<String> mListData2) {
        super(context, textViewResourceId, mListData2);
        this.mContext = context;
        this.mListData = mListData2;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView tv = (TextView) view;
        if (position == 0 && isHintShow) {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
        } else {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }
        return view;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if (position == 0 && isHintShow) {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorGray));
        } else {
            tv.setTextColor(ContextCompat.getColor(mContext, R.color.black));
            if (ValidationUtil.validateString(mSelectedOption) && mSelectedOption.equalsIgnoreCase((String) this.mListData.get(position))) {
                tv.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
            }
        }
        return view;
    }

    public void setSelectedOption(String selected_option) {
        mSelectedOption = selected_option;
    }

    public void setHintShow(boolean hintShow) {
        isHintShow = hintShow;
    }
}
