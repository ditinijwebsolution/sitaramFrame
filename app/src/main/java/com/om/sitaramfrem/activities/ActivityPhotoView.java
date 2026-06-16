package com.om.sitaramfrem.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.om.sitaramfrem.adapters.AdapterPhotoView;
import com.om.sitaramfrem.databinding.ActivityPhotoViewBinding;

import java.util.ArrayList;

public class ActivityPhotoView extends AppCompatActivity {

    private ActivityPhotoViewBinding binding;

    AdapterPhotoView mPhotoViewAdapter;

    private Context mContext;
    public static final String ITEMS = "items";
    public static final String POSITION = "pos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            finish();
            return;
        }

        ArrayList<String> mImageList = new ArrayList<>();

        if (bundle.getStringArrayList(ITEMS) != null) {
            mImageList.addAll(bundle.getStringArrayList(ITEMS));
        }

        mPhotoViewAdapter = new AdapterPhotoView(mContext, mImageList);
        binding.mViewPager.setAdapter(mPhotoViewAdapter);

        if (bundle.getInt(POSITION, -1) != -1) {
            binding.mViewPager.setCurrentItem(bundle.getInt(POSITION));
        }

        binding.mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
