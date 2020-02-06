package com.dong.edu.util;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dong.edu.data.Day;
import com.dong.edu.databinding.ViewholderDayBinding;

public class ViewHolderDay extends RecyclerView.ViewHolder {
    private ViewholderDayBinding dataBinding;
    private AdapterDaysList.DayClickListener mListener;
    private int mPosition;
    public ViewHolderDay(ViewholderDayBinding binding) {
        super(binding.getRoot());
        dataBinding = binding;
        binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDayItemClick(mPosition);
            }
        });
    }

    public void bindData(Day day, int position, AdapterDaysList.DayClickListener listener){
        dataBinding.dayNameViewholder.setText(day.getDescription());
        mPosition = position;
        mListener = listener;
    }
}
