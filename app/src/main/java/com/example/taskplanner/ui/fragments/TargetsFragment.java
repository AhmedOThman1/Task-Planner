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
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.taskplanner.R;
import com.example.taskplanner.RecyclerViewTouchListener;
import com.example.taskplanner.adapter.TargetsRecyclerViewAdapter;
import com.example.taskplanner.model.Target;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Collections;

import static com.example.taskplanner.ui.activities.MainActivity.Projects;
import static com.example.taskplanner.ui.fragments.CreateNewTargetFragment.uploadTarget;
import static com.example.taskplanner.ui.activities.MainActivity.Targets;
import static com.example.taskplanner.ui.fragments.HomeFragment.calculateNoOfColumns;
import static com.example.taskplanner.ui.fragments.HomeFragment.isDrawerOpen;
import static com.example.taskplanner.ui.activities.MainActivity.hideNav;
import static com.example.taskplanner.ui.activities.MainActivity.open_drawer;
import static com.example.taskplanner.ui.activities.MainActivity.showNav;

public class TargetsFragment extends Fragment {


    public static TargetsRecyclerViewAdapter adapter;
    RecyclerView grid_targets_recyclerview;


    public TargetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_targets, container, false);

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

        grid_targets_recyclerview = view.findViewById(R.id.grid_targets_recyclerview);
// sorting targets array list by target deadline
        Collections.sort(Targets, Target.targetsComparator);

        adapter = new TargetsRecyclerViewAdapter(getActivity());
        adapter.setTargets(Targets);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(calculateNoOfColumns(getContext()), StaggeredGridLayoutManager.VERTICAL);

        setTargetProgressBar();

        grid_targets_recyclerview.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), grid_targets_recyclerview, new RecyclerViewTouchListener.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (!Targets.get(position).getNote().equals("") || Targets.get(position).getSteps().size() != 0) {

                    Fragment one_target_fragment = new OneTargetFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt("project pos", position);
                    bundle.putInt("color", position % 4);
                    one_target_fragment.setArguments(bundle);
                    ((FragmentActivity) getActivity()).getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, one_target_fragment).commit();

                } else {
                    Toast.makeText(getActivity(), "This target has no details to show.", Toast.LENGTH_SHORT).show();
                    Log.w("Log", "pos : " + position + " , ste3p Size " + Targets.get(position).getSteps().size());
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                CardView card = view.findViewById(R.id.active_projects_card);
                if (card != null) {
                    card.setCardBackgroundColor(getResources().getColor(R.color.black));
                    deleteTarget(position, card);
                }
            }
        }));
        grid_targets_recyclerview.setAdapter(adapter);
        grid_targets_recyclerview.setLayoutManager(manager);

        grid_targets_recyclerview.addItemDecoration(new HomeFragment.ItemDecorationAlbumColumns(
                getResources().getDimensionPixelSize(R.dimen.photos_list_spacing), calculateNoOfColumns(getActivity())));

        return view;
    }

    AlertDialog.Builder builder;
    AlertDialog dialog;

    private void deleteTarget(int position, CardView card) {

        builder = new AlertDialog.Builder(getContext());

        View delete_dialog = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        TextView back = delete_dialog.findViewById(R.id.back_dialog),
                delete = delete_dialog.findViewById(R.id.delete_dialog),
        del_text = delete_dialog.findViewById(R.id.deltxt);

        String s = "Are you sure you want to delete " + Targets.get(position).getName()
                + " target with all steps inside it( " + Targets.get(position).getSteps().size() + " step ) ?";

        SpannableString del_message = new SpannableString(s);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        del_message.setSpan(boldSpan, 32, 32+Targets.get(position).getName().length()+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        del_text.setText(del_message);

        back.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Ok ♥️", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        delete.setOnClickListener(v -> {
            Targets.remove(position);
            adapter.notifyDataSetChanged();
            uploadTarget(FirebaseAuth.getInstance().getCurrentUser());
            Toast.makeText(getContext(), "target deleted \uD83D\uDE0D\uD83D\uDE48\uD83D\uDE48♥️", Toast.LENGTH_LONG).show();
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


    public static void setTargetProgressBar() {
        Log.w("Lol", "Targets.size() : " + Targets.size());
        for (int i = 0; i < Targets.size(); i++) {
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
