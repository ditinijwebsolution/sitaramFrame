package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.om.sitaramfrem.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

public class AdapterPhotoView extends PagerAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> data;

    public AdapterPhotoView(Context mContext, List<String> data) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        this.data = data;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View imageLayout = mInflater.inflate(R.layout.photoviewer_viewpager_layout, container, false);

        AVLoadingIndicatorView mAVLoadingIndicatorView = imageLayout.findViewById(R.id.mAVLoadingIndicatorView);
        PhotoView mPhotoView = imageLayout.findViewById(R.id.mPhotoView);
        Glide.with(mContext)
                .load(data.get(position))
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mAVLoadingIndicatorView.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(mPhotoView);
        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
