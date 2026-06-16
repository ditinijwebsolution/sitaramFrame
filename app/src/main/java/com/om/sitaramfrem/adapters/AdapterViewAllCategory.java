package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.om.sitaramfrem.databinding.RowViewAllCategoryBinding;
import com.om.sitaramfrem.listners.CategoryClickListener;
import com.om.sitaramfrem.models.view_all_category.ViewAllCategoryModel;

import java.util.List;

public class AdapterViewAllCategory extends RecyclerView.Adapter<AdapterViewAllCategory.ViewHolder> {
    private Context mContext;
    private List<ViewAllCategoryModel.Data.CategoryImage> dataList;
    private CategoryClickListener categoryClickListener;

    public AdapterViewAllCategory(Context mContext, List<ViewAllCategoryModel.Data.CategoryImage> dataList, CategoryClickListener categoryClickListener) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.categoryClickListener = categoryClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowViewAllCategoryBinding itemBinding = RowViewAllCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(dataList.get(position).getImage())
                .fitCenter()
                .into(holder.binding.imgCategory);
        holder.binding.mTxtFrameNumber.setText("Frame No : "+dataList.get(position).getFrameNo());
        if (dataList.get(position).isAddedToCart()) {
            holder.binding.btnAddToCart.setText("Added");
            holder.binding.btnAddToCart.setClickable(false);
        } else {
            holder.binding.btnAddToCart.setText("Add to cart");
            holder.binding.btnAddToCart.setClickable(true);
        }
        holder.binding.btnAddToCart.setVisibility(dataList.get(position).getActiveStatus()==1?View.VISIBLE:View.GONE);
        holder.binding.mTxtOutOfStock.setVisibility(dataList.get(position).getActiveStatus()==0?View.VISIBLE:View.GONE);

        holder.binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryClickListener.onAddToCartClick(position);
            }
        });

        holder.binding.getRoot().setOnClickListener(view -> {
            categoryClickListener.onCategoryClick(dataList.get(position));
            /*Intent intent = new Intent(mContext, ActivityAddOrder.class);
            intent.putExtra("ID", String.valueOf(dataList.get(position).getCategoryImageId()));
            mContext.startActivity(intent);*/
        });

        holder.binding.imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categoryClickListener.onDownloadClick(dataList.get(position).getImage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RowViewAllCategoryBinding binding;

        public ViewHolder(RowViewAllCategoryBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}