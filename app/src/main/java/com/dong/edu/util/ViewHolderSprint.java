package com.dong.edu.util;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dong.edu.R;
import com.dong.edu.data.Sprint;

import java.text.SimpleDateFormat;

public class ViewHolderSprint extends RecyclerView.ViewHolder {
    private TextView mSprintName;
    private TextView mSprintDescription;
    private TextView mStartedDate;
    private TextView mEndedDate;
    private TextView mStatus;
    private String mSprintID;
    private AdapterSprintList.SprintClickListener mListener;
    private String mStartedDateString;
    private String mEndedDateString;
    private SimpleDateFormat dateFormat;

    public ViewHolderSprint(@NonNull View itemView) {
        super(itemView);
        mSprintName = itemView.findViewById(R.id.sprint_name_viewholder);
        mSprintDescription = itemView.findViewById(R.id.sprint_description_viewholder);
        mStartedDate = itemView.findViewById(R.id.sprint_start_date_viewholder);
        mEndedDate = itemView.findViewById(R.id.sprint_end_date_viewholder);
        mStatus = itemView.findViewById(R.id.sprint_status);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSprintItemClick(mSprintID);
            }
        });
    }

    public void bind(Sprint sprint, String id, AdapterSprintList.SprintClickListener listener){
        mSprintName.setText(sprint.getSprintName());
        mSprintDescription.setText(sprint.message());
        mStartedDate.setText(sprint.startedDateString());
        mEndedDate.setText(sprint.endedDateString());
        mStatus.setText(sprint.status());

        mSprintID = id;
        mListener = listener;
    }



}
