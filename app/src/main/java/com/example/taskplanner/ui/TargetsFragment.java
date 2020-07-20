package com.example.taskplanner.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
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

import static com.example.taskplanner.ui.CreateNewTargetFragment.uploadTarget;
import static com.example.taskplanner.ui.MainActivity.Projects;
import static com.example.taskplanner.ui.MainActivity.Targets;
import static com.example.taskplanner.ui.HomeFragment.calculateNoOfColumns;
import static com.example.taskplanner.ui.HomeFragment.isDrawerOpen;
import static com.example.taskplanner.ui.MainActivity.hideNav;
import static com.example.taskplanner.ui.MainActivity.open_drawer;
import static com.example.taskplanner.ui.MainActivity.showNav;

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
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
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
                    Bundle bundle= new Bundle();
                    bundle.putInt("project pos", position);
                    bundle.putInt("color", position % 4);
                    one_target_fragment.setArguments(bundle);
                    ((FragmentActivity)getActivity()).getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).replace(R.id.frameLayout, one_target_fragment).commit();

                } else{
                    Toast.makeText(getActivity(), "This target has no details to show.", Toast.LENGTH_SHORT).show();
                    Log.w("Log","pos : "+position+" , ste3p Size "+ Targets.get(position).getSteps().size() );
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                deleteTarget(position);
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
    private void deleteTarget(int position) {

        builder = new AlertDialog.Builder(getContext());

        View sleep_dialog = getLayoutInflater().inflate(R.layout.delete_dialog, null);

        TextView back_dialog = sleep_dialog.findViewById(R.id.back_dialog),
                delete_dialog = sleep_dialog.findViewById(R.id.delete_dialog);

        back_dialog.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Ok ♥️", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        delete_dialog.setOnClickListener(v -> {
            Targets.remove(position);
            adapter.notifyDataSetChanged();
            uploadTarget(FirebaseAuth.getInstance().getCurrentUser());
            Toast.makeText(getContext(), "target deleted \uD83D\uDE0D\uD83D\uDE48\uD83D\uDE48♥️", Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        builder.setView(sleep_dialog).setCancelable(false);
        dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        //window.setLayout( ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public static void setTargetProgressBar() {
        Log.w("Lol","Targets.size() : "+Targets.size());
        for (int i = 0; i < Targets.size(); i++) {
            int process = 0;
            if (Targets.get(i).getSteps().size() != 0) {
                for (int j = 0; j < Targets.get(i).getSteps().size(); j++)
                {
                    if (Targets.get(i).getSteps().get(i)!=null&&Targets.get(i).getSteps().get(j).isCheck())
                        process++;

                    Log.w("TEST",i+": "+Targets.get(i).getSteps().get(j).getName()+" : "+Targets.get(i).getSteps().get(j).isCheck());
                }

                    Log.w("Log","target "+i+" Step Size : "+Targets.get(i).getSteps().size());
                    if(Targets.get(i).getSteps().size()!=0)
                Targets.get(i).setProgress(process * 100.0 / Targets.get(i).getSteps().size());
                    Log.w("LOG","progress : "+process +" * "+ 100.0 +" / "+ Targets.get(i).getSteps().size()+
                            " = "+process*100.0+" / "+Targets.get(i).getSteps().size()+" = " +Targets.get(i).getProgress());
            }
        }
        adapter.setTargets(Targets);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.w("Log","Resume");
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
