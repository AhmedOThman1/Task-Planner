package com.example.taskplanner.ui.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.taskplanner.R;
import com.example.taskplanner.RecyclerViewTouchListener;
import com.example.taskplanner.adapter.ChallengesRecyclerViewAdapter;
import com.example.taskplanner.adapter.TargetsRecyclerViewAdapter;
import com.example.taskplanner.model.Challenge;
import com.example.taskplanner.ui.viewModel.ChallengeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import static com.example.taskplanner.ui.activities.MainActivity.Challenges;
import static com.example.taskplanner.ui.activities.MainActivity.Targets;
import static com.example.taskplanner.ui.activities.MainActivity.hideNav;
import static com.example.taskplanner.ui.activities.MainActivity.open_drawer;
import static com.example.taskplanner.ui.activities.MainActivity.showNav;
import static com.example.taskplanner.ui.fragments.CreateNewTargetFragment.uploadTarget;
import static com.example.taskplanner.ui.fragments.CreateNewTaskFragment.CalendarToString;
import static com.example.taskplanner.ui.fragments.HomeFragment.calculateNoOfColumns;
import static com.example.taskplanner.ui.fragments.HomeFragment.isDrawerOpen;

public class ChallengesFragment extends Fragment {


    public static ChallengesRecyclerViewAdapter adapter;
    RecyclerView grid_challenges_recyclerview;
    FirebaseUser currentUser;

    public ChallengesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_challenges, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Challenges.clear();

//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.DAY_OF_MONTH,-75);
//        Challenges.add(new Challenge("Stop watching corn", CalendarToString(c),"Without corn",100));
//        c = Calendar.getInstance();
//        c.add(Calendar.DAY_OF_MONTH,-45);
//        Challenges.add(new Challenge("Stop doing corn", CalendarToString(c),"Without doing corn",90));
//        c = Calendar.getInstance();
//        c.add(Calendar.DAY_OF_MONTH,-85);
//        Challenges.add(new Challenge("Stop watching corn3", CalendarToString(c),"Without corn3",90));
//        c = Calendar.getInstance();
//        c.add(Calendar.DAY_OF_MONTH,-25);
//        Challenges.add(new Challenge("Stop watching corn4", CalendarToString(c),"Without corn4",100));
//        c = Calendar.getInstance();
//        Challenges.add(new Challenge("Stop watching corn5", CalendarToString(c),"Without corn4",10));

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        getActivity().setTitle("");
//
//        if ( ((AppCompatActivity)getActivity()).getSupportActionBar() != null)
//        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.yellow));
        }
        open_drawer = view.findViewById(R.id.open_drawer);
        isDrawerOpen = false;
        open_drawer.setOnClickListener(v -> {
            if (isDrawerOpen) {
                hideNav();
                open_drawer.setImageResource(R.drawable.ic_menu);
                isDrawerOpen = false;
            } else {
                close_keyboard();
                showNav();
                open_drawer.setImageResource(R.drawable.ic_back);
                isDrawerOpen = true;
            }
        });

        grid_challenges_recyclerview = view.findViewById(R.id.grid_challenges_recyclerview);
// sorting Challenges array list by challenge start time
        Collections.sort(Challenges, Challenge.challengesComparator);
        adapter = new ChallengesRecyclerViewAdapter(getActivity());
        adapter.setChallenges(Challenges);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(calculateNoOfColumns(getContext()), StaggeredGridLayoutManager.VERTICAL);

//        setTargetProgressBar();

        grid_challenges_recyclerview.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), grid_challenges_recyclerview, new RecyclerViewTouchListener.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Fragment one_challenge_fragment = new OneChallengeFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("challenge pos", position);
                one_challenge_fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, one_challenge_fragment).commit();

            }

            @Override
            public void onLongClick(View view, int position) {
                CardView card = view.findViewById(R.id.active_projects_card);
                if (card != null) {
                    card.setCardBackgroundColor(getResources().getColor(R.color.black));
                    deleteChallenge(position, card);
                }
            }
        }));
        grid_challenges_recyclerview.setAdapter(adapter);
        grid_challenges_recyclerview.setLayoutManager(manager);

        grid_challenges_recyclerview.addItemDecoration(new HomeFragment.ItemDecorationAlbumColumns(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing), calculateNoOfColumns(getActivity())));


        ChallengeViewModel challengeViewModel = new ViewModelProvider(this).get(ChallengeViewModel.class);
        if(Challenges.isEmpty())
            challengeViewModel.getChallenges(currentUser);
        challengeViewModel.ChallengesMutableLiveData.observe(getActivity(), challenges -> {
            Challenges.clear();
            Challenges.addAll(challenges);
            adapter.setChallenges(Challenges);
            adapter.notifyDataSetChanged();
        });
        return view;
    }

    AlertDialog.Builder builder;
    AlertDialog dialog;

    private void deleteChallenge(int position, CardView card) {

        builder = new AlertDialog.Builder(getContext());
        View delete_dialog = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        TextView back = delete_dialog.findViewById(R.id.back_dialog),
                delete = delete_dialog.findViewById(R.id.delete_dialog),
                del_text = delete_dialog.findViewById(R.id.deltxt);

        String s = getActivity().getResources().getString(R.string.are_you_sure_you_want_to_delete ) +" "+ Challenges.get(position).getName()
                +  getActivity().getResources().getString(R.string.challenge_question);

        SpannableString del_message = new SpannableString(s);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        del_message.setSpan(boldSpan, getActivity().getResources().getInteger(R.integer.bold_index), getActivity().getResources().getInteger(R.integer.bold_index) + Targets.get(position).getName().length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        del_text.setText(del_message);

        back.setOnClickListener(v -> {
            Toast.makeText(getContext(), getActivity().getResources().getString(R.string.ok_nothing_deleted), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        delete.setOnClickListener(v -> {
            Challenges.remove(position);
            adapter.notifyDataSetChanged();
            uploadChallenges(currentUser);
            Toast.makeText(getContext(), getActivity().getResources().getString(R.string.challenge_deleted), Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        builder.setView(delete_dialog);
        dialog = builder.create();
        dialog.show();
        dialog.setOnDismissListener(dialog -> {
            if (card != null)
                if (position % 4 == 0) {
                    card.setCardBackgroundColor(getActivity().getResources().getColor(R.color.zeti));
                } else if (position % 4 == 1) {
                    card.setCardBackgroundColor(getActivity().getResources().getColor(R.color.red));
                } else if (position % 4 == 2) {
                    card.setCardBackgroundColor(getActivity().getResources().getColor(R.color.yellow));
                } else if (position % 4 == 3) {
                    card.setCardBackgroundColor(getActivity().getResources().getColor(R.color.blue));
                }
        });
        Window window = dialog.getWindow();
        //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public static void uploadChallenges(FirebaseUser currentUser) {
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child(currentUser.getUid() + "/Challenges/").setValue(Challenges);
        Log.w("Lol2", "-----------------create , End of upload Challenges -------------------");
    }
/*

    public static void setTargetProgressBar() {
        Log.w("Lol", "Targets.size() : " + Targets.size());
        for (int i = 0; i < Challenges.size(); i++) {
            int process = 0;
            if (Targets.get(i).getSteps().size() != 0) {
                for (int j = 0; j < Targets.get(i).getSteps().size(); j++) {
                    if (i<Targets.get(i).getSteps().size()&&Targets.get(i).getSteps().get(i) != null && Targets.get(i).getSteps().get(j).isCheck())
                        process++;

                    Log.w("TEST", i + ": " + Targets.get(i).getSteps().get(j).getName() + " : " + Targets.get(i).getSteps().get(j).isCheck());
                }

                Log.w("Log", "target " + i + " Step Size : " + Targets.get(i).getSteps().size());
                if (Targets.get(i).getSteps().size() != 0)
                    Targets.get(i).setProgress(process * 100 / Targets.get(i).getSteps().size());
            }
        }
        adapter.setTargets(Targets);
        adapter.notifyDataSetChanged();
    }
*/

    @Override
    public void onResume() {
        super.onResume();
        Log.w("Log", "Resume");
        adapter.notifyDataSetChanged();
    }

    void close_keyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);     // Context.INPUT_METHOD_SERVICE
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
