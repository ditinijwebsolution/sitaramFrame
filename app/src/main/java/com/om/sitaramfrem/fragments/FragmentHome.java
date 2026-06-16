package com.om.sitaramfrem.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;
import com.om.sitaramfrem.R;
import com.om.sitaramfrem.activities.ActivityAddOrder;
import com.om.sitaramfrem.adapters.AdapterFragmentPager;
import com.om.sitaramfrem.adapters.AdapterOrderList;
import com.om.sitaramfrem.api.APIClient;
import com.om.sitaramfrem.api.ApiCall;
import com.om.sitaramfrem.api.ApiCallback;
import com.om.sitaramfrem.api.StandardStatusCodes;
import com.om.sitaramfrem.databinding.FragmentHomeBinding;
import com.om.sitaramfrem.event.UpdateOrderList;
import com.om.sitaramfrem.models.orderlist.OrderModel;
import com.om.sitaramfrem.utils.PrintLog;
import com.om.sitaramfrem.utils.SessionUtil;
import com.om.sitaramfrem.utils.ValidationUtil;
import com.om.superrecyclerview.OnMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;


public class FragmentHome extends Fragment {
    private FragmentHomeBinding binding;
    private Context mContext;

    public FragmentHome() {
        // Required empty public constructor
    }

    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AdapterFragmentPager mAdapterFragmentPager = new AdapterFragmentPager(this);
        mAdapterFragmentPager.addFragment(FragmentPendingOrder.newInstance());
        mAdapterFragmentPager.addFragment(FragmentInprogressOrder.newInstance());
        mAdapterFragmentPager.addFragment(FragmentCompletedOrder.newInstance());
        binding.mOrderViewPager.setAdapter(mAdapterFragmentPager);

        new TabLayoutMediator(binding.mOrderTab, binding.mOrderViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText(mContext.getString(R.string.lbl_tab_pending_order));
                        break;
                    case 1:
                        tab.setText(mContext.getString(R.string.lbl_tab_inprogress_order));
                        break;
                    case 2:
                        tab.setText(mContext.getString(R.string.lbl_tab_completed_order));
                        break;
                }
            }
        }).attach();

    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}