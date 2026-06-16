package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.om.sitaramfrem.activities.ActivityViewAllCategory;
import com.om.sitaramfrem.databinding.RowCategoryBinding;
import com.om.sitaramfrem.databinding.RowHomeParentBinding;
import com.om.sitaramfrem.listners.CategoryClickListener;
import com.om.sitaramfrem.listners.HomeClickListener;
import com.om.sitaramfrem.models.categorylist.CategoryModel;

import java.util.List;

public class AdapterHome extends RecyclerView.Adapter<AdapterHome.ViewHolder> {
    private List<CategoryModel.Data> dataList;
    private Context mContext;
    private HomeClickListener homeClickListener;

    public AdapterHome(Context mContext, List<CategoryModel.Data> dataList, HomeClickListener homeClickListener) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.homeClickListener = homeClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowHomeParentBinding itemBinding = RowHomeParentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.binding.textTitle.setText(dataList.get(position).getName());

        holder.binding.recHome.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        AdapterCategory mAdapterCategory = new AdapterCategory(mContext, position,dataList.get(position).getCategoryImage(), homeClickListener);
        holder.binding.recHome.setAdapter(mAdapterCategory);

        holder.binding.buttonSeeAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ActivityViewAllCategory.class);
                intent.putExtra("ID", dataList.get(position).getId());
                intent.putExtra("category_name", dataList.get(position).getName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RowHomeParentBinding binding;

        public ViewHolder(RowHomeParentBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}