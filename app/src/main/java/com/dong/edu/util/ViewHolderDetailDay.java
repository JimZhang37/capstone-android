package com.dong.edu.util;


import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.dong.edu.data.Day;
import com.dong.edu.databinding.ViewholderDetailDayBinding;
import com.squareup.picasso.Picasso;

public class ViewHolderDetailDay extends RecyclerView.ViewHolder {

    private ViewholderDetailDayBinding dataBinding;


    public ViewHolderDetailDay(ViewholderDetailDayBinding binding) {
        super(binding.getRoot());
        dataBinding = binding;

    }

    public void bindData(Day day){
        dataBinding.tvDayDescription.setText(day.getDescription());
        Picasso.get().load(day.getPictureUri()).into(dataBinding.ivDayImage);

    }
}
