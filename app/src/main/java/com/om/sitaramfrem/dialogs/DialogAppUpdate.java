package com.om.sitaramfrem.dialogs;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.om.sitaramfrem.databinding.DialogAppUpdateBinding;

public class DialogAppUpdate extends DialogFragment {

    private Context mContext;
    private OnDialogListener onDialogListener;
    private DialogAppUpdateBinding binding;

    public static final String TAG = DialogAppUpdate.class.getSimpleName();

    public interface OnDialogListener {
        void onDialogItemClick(String action);
    }

    public DialogAppUpdate() {

    }

    public static DialogAppUpdate newInstance() {
        return new DialogAppUpdate();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAppUpdateBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            if (getDialog() != null
                    && getDialog().getWindow() != null) {
                getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDialogListener != null) {
                    onDialogListener.onDialogItemClick("update");
                }
            }
        });
    }

    public void setOnDialogListener(OnDialogListener onDialogListener) {
        this.onDialogListener = onDialogListener;
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
