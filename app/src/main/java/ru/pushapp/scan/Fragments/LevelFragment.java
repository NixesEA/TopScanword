package ru.pushapp.scan.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ru.pushapp.scan.Adapters.LevelData;
import ru.pushapp.scan.App;
import ru.pushapp.scan.R;
import ru.pushapp.scan.Adapters.RecyclerAdapter;

public class LevelFragment extends Fragment implements View.OnClickListener{

    ArrayList<LevelData> leaderList;
    RecyclerAdapter adapter;

    RecyclerView level_rv;
    Toolbar toolbar;
    ActionBar actionbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lvl_fragment, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        actionbar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(R.layout.custom_actionbar);

        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        actionbar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        level_rv = view.findViewById(R.id.level_rv);
        level_rv.setLayoutManager(linearLayoutManager);

        leaderList = new ArrayList<>();

        return view;
    }

    @Override
    public void onResume() {
        level_rv.setAdapter(null);

//        leaderList =
        getArrayList();
        adapter = new RecyclerAdapter(getContext(), leaderList);

        level_rv.setAdapter(adapter);
        super.onResume();
    }


    private void getArrayList() {

        leaderList.clear();

        ArrayList<LevelData> arrayList = new ArrayList<>();
        int size = App.getCrosswordSize();
        for (int i = 0; i < App.getCrosswordSize(); i++){
            LevelData levelData = App.getCrosswordInfo(i);

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(levelData.getRes(), Context.MODE_PRIVATE);
            levelData.setProgress((int) (sharedPreferences.getFloat("progress", 0) * 100));
            if (!levelData.getUnblocked()){
                levelData.setUnblocked((sharedPreferences.getBoolean("unblocked", false)));
            }

            leaderList.add(levelData);
        }

        return;
    }

    @Override
    public void onClick(View view) {
        getActivity().onBackPressed();
    }

}
