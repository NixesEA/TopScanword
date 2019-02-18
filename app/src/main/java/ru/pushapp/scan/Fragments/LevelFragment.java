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

public class LevelFragment extends Fragment implements View.OnClickListener {

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

        return view;
    }

    @Override
    public void onResume() {
        ArrayList<LevelData> leaderList = getArrayList();
        RecyclerAdapter adapter = new RecyclerAdapter(getContext(), leaderList);
        level_rv.setAdapter(adapter);
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        App.getAllRaw();
    }

    private ArrayList<LevelData> getArrayList() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("scanword_1", Context.MODE_PRIVATE);
        int progress = (int) (sharedPreferences.getFloat("progress", 0) * 100);

        ArrayList<LevelData> arrayList = new ArrayList<>();
        arrayList.add(new LevelData("Сканворд №1", "Тематика: Общая", 73, true, "scanword_1"));
        arrayList.add(new LevelData("Сканворд №2", "Тематика: Общая", 15, true, "scanword_2"));
        arrayList.add(new LevelData("Сканворд №3", "Тематика: Общая", progress, true, ""));
        arrayList.add(new LevelData("Сканворд №4", "Тематика: Общая", 0, false, ""));
        arrayList.add(new LevelData("Сканворд №5", "Тематика: Общая", 0, false, ""));
        return arrayList;
    }

    @Override
    public void onClick(View view) {
        getActivity().onBackPressed();
    }
}
