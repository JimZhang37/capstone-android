package com.dong.edu.util;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dong.edu.data.Day;
import com.dong.edu.databinding.ViewholderDayBinding;

public class ViewHolderDay extends RecyclerView.ViewHolder {
    private ViewholderDayBinding dataBinding;

    public ViewHolderDay(ViewholderDayBinding binding) {
        super(binding.getRoot());
        dataBinding = binding;
    }

    public void bindData(Day day){
        dataBinding.dayNameViewholder.setText(day.getDescription());
    }
}
