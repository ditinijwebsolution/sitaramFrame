package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.databinding.RowOrderListBinding;
import com.om.sitaramfrem.models.orderlist.OrderModel;
import com.om.sitaramfrem.utils.ValidationUtil;

import java.util.List;

public class AdapterOrderList extends RecyclerView.Adapter<AdapterOrderList.ViewHolder> {
    private Context mContext;
    private List<OrderModel.Data> dataList;

    public AdapterOrderList(Context mContext, List<OrderModel.Data> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowOrderListBinding itemBinding = RowOrderListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (!dataList.get(position).getCategoryImage().isEmpty()) {
            Glide.with(holder.itemView)
                    .load(dataList.get(position).getCategoryImage())
                    .fitCenter()
                    .into(holder.binding.imageCategory);

            holder.binding.imageCategory.setVisibility(View.VISIBLE);
        } else {
            holder.binding.imageCategory.setVisibility(View.GONE);
        }

        if (ValidationUtil.validateString(dataList.get(position).getDate())) {
            holder.binding.mTxtDate.setText(dataList.get(position).getDate());
        }

        if (ValidationUtil.validateString(dataList.get(position).getFrameMeasure())) {
            holder.binding.mTxtFrameMeasure.setText(dataList.get(position).getFrameMeasure());
        }

        if (ValidationUtil.validateString(dataList.get(position).getOrderNo())) {
            holder.binding.mTxtOrderNo.setText(dataList.get(position).getOrderNo());
        }

        if (ValidationUtil.validateString(dataList.get(position).getQuantity())) {
            holder.binding.mTxtQty.setText(dataList.get(position).getQuantity());
        }

        if (ValidationUtil.validateString(dataList.get(position).getItemName())) {
            holder.binding.mTxtItemName.setText(dataList.get(position).getItemName());
        }

        if (ValidationUtil.validateString(dataList.get(position).getFrameNo())) {
            holder.binding.mTxtFrameNo.setText(dataList.get(position).getFrameNo());
        }

        if (ValidationUtil.validateString(dataList.get(position).getOrderStatus())) {
            holder.binding.mTxtOrderStatus.setText(dataList.get(position).getOrderStatus());
        }

        if (ValidationUtil.validateString(dataList.get(position).getNotes())) {
            holder.binding.mTxtNotes.setText(dataList.get(position).getNotes());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RowOrderListBinding binding;

        public ViewHolder(RowOrderListBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}
