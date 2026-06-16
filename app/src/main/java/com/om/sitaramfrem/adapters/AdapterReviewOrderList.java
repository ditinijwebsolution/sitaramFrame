package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.om.sitaramfrem.databinding.RowOrderReviewListBinding;
import com.om.sitaramfrem.models.OrderModel;
import com.om.sitaramfrem.utils.ValidationUtil;

import java.util.List;

public class AdapterReviewOrderList extends RecyclerView.Adapter<AdapterReviewOrderList.ViewHolder> {
    private Context mContext;
    private List<OrderModel> dataList;

    public AdapterReviewOrderList(Context mContext, List<OrderModel> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowOrderReviewListBinding itemBinding = RowOrderReviewListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (ValidationUtil.validateString(dataList.get(position).getFrameMeasure())) {
            holder.binding.mTxtFrameMeasure.setText(dataList.get(position).getFrameMeasure());
        }

        if (ValidationUtil.validateString(dataList.get(position).getQuantity())) {
            holder.binding.mTxtQty.setText(dataList.get(position).getQuantity());
        }

        if (ValidationUtil.validateString(dataList.get(position).getItemName())) {
            holder.binding.mTxtItemName.setText(dataList.get(position).getItemName());
        }

        if (ValidationUtil.validateString(dataList.get(position).getFrameName())) {
            holder.binding.mTxtFrameNo.setText(dataList.get(position).getFrameName());
        }

        if (ValidationUtil.validateString(dataList.get(position).getNotes())) {
            holder.binding.mTxtNotes.setText(dataList.get(position).getNotes());
        }

        if(dataList.get(position).getImgUri()!=null){
            holder.binding.mIvFrameImg.setVisibility(View.VISIBLE);
            holder.binding.mIvFrameImg.setImageURI(dataList.get(position).getImgUri());
        }else if(!dataList.get(position).getImgPath().isEmpty()){
            holder.binding.mIvFrameImg.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(dataList.get(position).getImgPath())
                    .centerCrop()
                    .into(holder.binding.mIvFrameImg);
        }else{
            holder.binding.mIvFrameImg.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RowOrderReviewListBinding binding;

        public ViewHolder(RowOrderReviewListBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}
