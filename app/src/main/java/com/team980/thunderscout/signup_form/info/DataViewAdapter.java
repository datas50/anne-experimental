package com.team980.thunderscout.signup_form.info;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.team980.thunderscout.signup_form.data.StudentData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

//TODO reimplement class
public class DataViewAdapter extends RecyclerView.Adapter<DataViewAdapter.StudentViewHolder> {

    private LayoutInflater mInflator;

    private Context context;

    public DataViewAdapter(Context context) {
        super();

        mInflator = LayoutInflater.from(context); //TODO move to ViewGroup.getContext()

        this.context = context;
    }

    // onCreate ...
    @Override
    public StudentViewHolder onCreateViewHolder(ViewGroup parentViewGroup, int i) {
        View teamView = mInflator.inflate(R.layout.team_view, parentViewGroup, false);
        return new StudentViewHolder(teamView);
    }

    // onBind ...
    @Override
    public void onBindViewHolder(StudentViewHolder teamViewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    /**
     * Adds an entry to the view. Called by the database reader.
     *
     * @param data StudentData to insert
     */
    public void addStudentData(StudentData data) {
        Log.d("Adding Data", "Fetching parent item list");

        for (int i = 0; i < teams.size(); i++) {
            TeamWrapper tw = teams.get(i);
            Log.d("Adding Data", "Looping: " + i);

            if (tw.getTeamNumber().equals(data.getTeamNumber())) {
                //Pre-existing team
                Log.d("Adding Data", "Pre existing team: " + data.getTeamNumber());

                ArrayList<StudentData> childList = (ArrayList<StudentData>) tw.getChildItemList();

                Log.d("Adding Data", "Fetching child item list");

                for (StudentData child : childList) {
                    Log.d("Adding Data", "Looping child: " + child.getTeamNumber());
                    if (child.getDateAdded() == (data.getDateAdded())) { //TODO verify this works
                        //This child has already been added to the database
                        Log.d("Adding Data", "Child already in DB");
                        return;
                    }
                }

                childList.add(data);
                Log.d("Adding Data", "Adding new child to parent");
                notifyChildItemInserted(i, childList.size() - 1); //TODO verify this
                notifyParentItemChanged(i); //This forces the parent to update

                sort(sortMode);
                return;
            }
        }
        //New team
        Log.d("Adding Data", "Adding new parent to list");
        teams.add(new TeamWrapper(data.getTeamNumber(), data));
        notifyParentItemInserted(teams.size() - 1); //TODO verify this

        sort(sortMode);
    }

    /**
     * Removes all the data from the list.
     * Called by the database emptier.
     */
    public void clearData() {
        notifyParentItemRangeRemoved(0, teams.size());
        getItemList().removeAll(teams);
    }

    public class StudentViewHolder extends RecyclerView.ViewHolder {

        private TextView dateAdded;

        private ImageButton infoButton;

        public StudentViewHolder(View itemView) {
            super(itemView);

            dateAdded = (TextView) itemView.findViewById(R.id.scout_dateAdded);

            infoButton = (ImageButton) itemView.findViewById(R.id.scout_infoButton);
        }

        public void bind(final StudentData studentData) {
            dateAdded.setText(SimpleDateFormat.getDateTimeInstance().format(studentData.getDateAdded()));

            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent launchInfoActivity = new Intent(context, InfoActivity.class);
                    launchInfoActivity.putExtra("com.team980.thunderscout.INFO_SCOUT", studentData);
                    context.startActivity(launchInfoActivity);
                }
            });

            infoButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent launchInfoActivity = new Intent(context, InfoActivity.class);
                    launchInfoActivity.putExtra("com.team980.thunderscout.INFO_SCOUT", studentData);
                    context.startActivity(launchInfoActivity);
                }
            });
        }
    }


}
