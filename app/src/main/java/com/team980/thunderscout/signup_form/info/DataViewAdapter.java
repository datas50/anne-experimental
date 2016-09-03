package com.team980.thunderscout.signup_form.info;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team980.thunderscout.signup_form.R;
import com.team980.thunderscout.signup_form.data.StudentData;

import java.util.List;

public class DataViewAdapter extends RecyclerView.Adapter<DataViewAdapter.StudentViewHolder> {

    private LayoutInflater mInflator;

    private List<StudentData> studentData;

    private Context context;

    public DataViewAdapter(Context context, List<StudentData> data) {
        super();

        mInflator = LayoutInflater.from(context); //TODO move to ViewGroup.getContext()

        this.context = context;

        this.studentData = data;
    }

    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parentViewGroup, int i) {
        View studentView = mInflator.inflate(R.layout.student_view, parentViewGroup, false);
        return new StudentViewHolder(studentView);
    }

    @Override
    public void onBindViewHolder(StudentViewHolder view, int position) {
        StudentData data = studentData.get(position);

        view.name.setText(data.getName());    }

    @Override
    public int getItemCount() {
        return studentData.size();
    }

    /**
     * Adds an entry to the view. Called by the database reader.
     *
     * @param data StudentData to insert
     */
    public void addStudentData(StudentData data) {
       studentData.add(data);
       notifyItemInserted(studentData.size());
    }

    /**
     * Removes all the data from the list.
     * Called by the database emptier.
     */
    public void clearData() {
        notifyItemRangeRemoved(0, studentData.size());
        studentData.removeAll(studentData);
    }

    public List<StudentData> getDataList() {
        return studentData;
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView name;

        public StudentViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.student_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent launchInfoActivity = new Intent(context, InfoActivity.class);
            launchInfoActivity.putExtra("com.team980.thunderscout.signup_form.INFO_STUDENT", studentData.get(getAdapterPosition()));
            context.startActivity(launchInfoActivity);
        }
    }


}
