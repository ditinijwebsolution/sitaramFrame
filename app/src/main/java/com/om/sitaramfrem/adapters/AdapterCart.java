package com.om.sitaramfrem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.om.sitaramfrem.activities.ActivityCartList;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.databinding.RowCartListBinding;
import com.om.sitaramfrem.listners.CartClickListener;
import com.om.sitaramfrem.models.CommonRes;
import com.om.sitaramfrem.models.cart_list.CartListModel;
import com.om.sitaramfrem.utils.AppUtil;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.ViewHolder> {
    private Context mContext;
    private List<CartListModel.Data> dataList;
    private SessionUtil mSessionUtil;
    private CartClickListener cartClickListener;

    private static final String TAG = ActivityCartList.class.getSimpleName();

    public AdapterCart(Context mContext, List<CartListModel.Data> dataList, SessionUtil mSessionUtil, CartClickListener cartClickListener) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.mSessionUtil = mSessionUtil;
        this.cartClickListener = cartClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCartListBinding itemBinding = RowCartListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(dataList.get(position).getImage())
                .fitCenter()
                .into(holder.binding.imgCategory);

        holder.binding.textDate.setText(dataList.get(position).getDate());
        holder.binding.textQty.setText(dataList.get(position).getQuantity());

        holder.binding.imageRemoveQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataList.get(position).getQuantity().equalsIgnoreCase("1")) {
                    cartClickListener.onRemoveCartClick(position);
                    //removeFromCartCall(position);
                } else {
                    int qty = Integer.parseInt(dataList.get(position).getQuantity()) - 1;
                    cartClickListener.onEditCartClick(position, "0", qty);
                    //editCartCall(position, "0", qty);
                }
            }
        });

        holder.binding.imageAddQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(dataList.get(position).getQuantity()) + 1;
                cartClickListener.onEditCartClick(position, "1", qty);
                //editCartCall(position, "1", qty);
            }
        });

        holder.binding.getRoot().setOnClickListener(view -> {

        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        RowCartListBinding binding;

        public ViewHolder(RowCartListBinding itemBinding) {
            super(itemBinding.getRoot());
            binding = itemBinding;
        }
    }
}