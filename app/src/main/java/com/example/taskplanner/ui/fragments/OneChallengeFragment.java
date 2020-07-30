package com.example.taskplanner.ui.fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskplanner.R;
import com.example.taskplanner.adapter.OneTargetStepAdapter;
import com.example.taskplanner.model.Challenge;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Currency;
import java.util.Objects;

import static com.example.taskplanner.ui.activities.MainActivity.Targets;
import static com.example.taskplanner.ui.fragments.ChallengesFragment.CurrentChallenges;
import static com.example.taskplanner.ui.fragments.ChallengesFragment.DoneChallenges;
import static com.example.taskplanner.ui.fragments.ChallengesFragment.uploadChallenges;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.CalendarToString;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.StringToCalendar;

public class OneChallengeFragment extends Fragment {

    int position;
    private ProgressBar challengeProgressBar;
    private TextView stop_challenge_count, set_challenge_count_zero, challenge_start_date,
            challenge_end_date, num_of_days2, num_of_days, num_of_days_to, challenge_days, challenge_name;
    FirebaseUser currentUser;
    Challenge challenge;

    public OneChallengeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one_challenge, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        challengeProgressBar = view.findViewById(R.id.challengeProgressBar);
        stop_challenge_count = view.findViewById(R.id.stop_challenge_count);
        set_challenge_count_zero = view.findViewById(R.id.set_challenge_count_zero);
        challenge_start_date = view.findViewById(R.id.challenge_start_date);
        challenge_end_date = view.findViewById(R.id.challenge_end_date);
        num_of_days2 = view.findViewById(R.id.num_of_days2);
        num_of_days = view.findViewById(R.id.num_of_days);
        num_of_days_to = view.findViewById(R.id.num_of_days_to);
        challenge_days = view.findViewById(R.id.challenge_days);
        challenge_name = view.findViewById(R.id.challenge_name);

//        if (((AppCompatActivity)getActivity()).getSupportActionBar() != null)
//            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Bundle bundle = getArguments();
        assert bundle != null;
        position = bundle.getInt("challenge pos", -1);
        int t = bundle.getInt("type", -1);
        if (t == 0)
            challenge = CurrentChallenges.get(position);
        else
            challenge = DoneChallenges.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.dark_blue));
        }

        challenge_name.setText(challenge.getName());
//        toolbar.setTitle(Challenges.get(position).getName());
        init();

        stop_challenge_count.setOnClickListener(v -> {

        });
        set_challenge_count_zero.setOnClickListener(v -> {

            //TODO put it inside dialog alert
            if (t == 0)
                CurrentChallenges.get(position).setStartDate(CalendarToString(Calendar.getInstance()));
            else
                DoneChallenges.get(position).setStartDate(CalendarToString(Calendar.getInstance()));

            uploadChallenges(currentUser);
            init();
        });

        return view;
    }

    private void init() {

        Calendar start = (Calendar) StringToCalendar(challenge.getStartDate()).clone();
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_MONTH, challenge.getDuration());
        long difference = Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis();
        difference = (difference / 1000 / 60 / 60 / 24);

        String diff = "" + (difference > challenge.getDuration() ? challenge.getDuration() : difference  >= 0 ? difference : 0),
                diff2 = "" + ((challenge.getDuration() - difference) > 0
                        ? ( (difference) >= 0 ? (challenge.getDuration() - difference) : challenge.getDuration() ) : 0),
                temp = diff + " / " + challenge.getDuration(),
                days_to_without = getActivity().getResources().getString(R.string.number_of_days) + " " + challenge.getDays_to_without(),
                start_time = start.get(Calendar.DAY_OF_MONTH) + "/" + (start.get(Calendar.MONTH) + 1) + "/" + start.get(Calendar.YEAR),
                end_time = end.get(Calendar.DAY_OF_MONTH) + "/" + (end.get(Calendar.MONTH) + 1) + "/" + end.get(Calendar.YEAR);
        challenge_start_date.setText(start_time);
        challenge_end_date.setText(end_time);
        num_of_days.setText(diff);
        num_of_days2.setText(diff2);
        challenge_days.setText(temp);
        num_of_days_to.setText(days_to_without);
        challengeProgressBar.setMax(2 * challenge.getDuration());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            challengeProgressBar.setProgress((int) (difference), true);
        } else
            challengeProgressBar.setProgress((int) (difference));

    }
}
