package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.om.sitaramfrem.databinding.RowInactiveCategoryBinding;
import com.om.sitaramfrem.listners.InActiveCategoryClickListener;
import com.om.sitaramfrem.models.inactive_category.InActiveCategoryModel;

import java.util.List;

public class AdapterInActiveCategory extends RecyclerView.Adapter<AdapterInActiveCategory.ViewHolder> {
    private Context mContext;
    private List<InActiveCategoryModel.Data> dataList;
    private InActiveCategoryClickListener categoryClickListener;

    public AdapterInActiveCategory(Context mContext, List<InActiveCategoryModel.Data> dataList, InActiveCategoryClickListener categoryClickListener) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.categoryClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowInactiveCategoryBinding itemBinding = RowInactiveCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(dataList.get(position).getImage())
                .fitCenter()
                .into(holder.binding.imgCategory);

        holder.binding.mTxtFrameNumber.setText("Frame No : "+dataList.get(position).getFrameNo());

        holder.binding.getRoot().setOnClickListener(view -> {
            categoryClickListener.onCategoryClick(dataList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RowInactiveCategoryBinding binding;

        public ViewHolder(RowInactiveCategoryBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}