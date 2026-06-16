package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.databinding.RowOrderListBinding;
import com.om.sitaramfrem.databinding.RowSliderBinding;
import com.om.sitaramfrem.models.orderlist.OrderModel;
import com.om.sitaramfrem.models.slider.SliderData;
import com.om.sitaramfrem.models.slider.SliderModel;
import com.om.sitaramfrem.utils.ValidationUtil;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class AdapterSlider extends SliderViewAdapter<AdapterSlider.ViewHolder> {
    private List<SliderModel.Data> dataList;

    public AdapterSlider(Context mContext, List<SliderModel.Data> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        RowSliderBinding itemBinding = RowSliderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(dataList.get(position).getImage())
                .fitCenter()
                .into(holder.binding.imageSlider);
    }


    @Override
    public int getCount() {
        return dataList.size();
    }

    class ViewHolder extends SliderViewAdapter.ViewHolder {
        RowSliderBinding binding;

        public ViewHolder(RowSliderBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}