package com.team980.thunderscout.signup_form.recruit;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team980.thunderscout.signup_form.R;

public class SignupFragment extends Fragment {

    ScoutActivity scoutActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        scoutActivity = (ScoutActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
