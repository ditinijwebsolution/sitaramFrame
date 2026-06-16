package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.activities.ActivityAddOrder;
import com.om.sitaramfrem.activities.ActivityPhotoView;
import com.om.sitaramfrem.databinding.RowCategoryBinding;
import com.om.sitaramfrem.databinding.RowOrderListBinding;
import com.om.sitaramfrem.databinding.RowSliderBinding;
import com.om.sitaramfrem.listners.CategoryClickListener;
import com.om.sitaramfrem.listners.HomeClickListener;
import com.om.sitaramfrem.models.categorylist.CategoryModel;
import com.om.sitaramfrem.models.slider.SliderModel;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.ViewHolder> {
    private Context mContext;
    int mainPosition;
    private List<CategoryModel.Data.CategoryImage> dataList;
    private HomeClickListener homeClickListener;

    public AdapterCategory(Context mContext, int mainPosition, List<CategoryModel.Data.CategoryImage> dataList, HomeClickListener homeClickListener) {
        this.mContext = mContext;
        this.mainPosition = mainPosition;
        this.dataList = dataList;
        this.homeClickListener = homeClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCategoryBinding itemBinding = RowCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(dataList.get(position).getImage())
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
                homeClickListener.onAddToCartClick(mainPosition, position);
                /*Intent intent = new Intent(mContext, ActivityAddOrder.class);
                intent.putExtra("ID", String.valueOf(dataList.get(position).getCategoryImageId()));
                intent.putExtra("CART_LIST", new ArrayList());
                mContext.startActivity(intent);*/
            }
        });

        holder.binding.imgCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> items = new ArrayList<>();
                items.add(dataList.get(position).getImage());
                Intent intent = new Intent(mContext, ActivityPhotoView.class);
                intent.putExtra(ActivityPhotoView.ITEMS,items);
                mContext.startActivity(intent);
            }
        });

        holder.binding.imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeClickListener.onDownloadClick(dataList.get(position).getImage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RowCategoryBinding binding;

        public ViewHolder(RowCategoryBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}