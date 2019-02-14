package ru.pushapp.scan.Fragments;

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

        ArrayList<LevelData> leaderList = getArrayList();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        RecyclerAdapter adapter = new RecyclerAdapter(getContext(), leaderList);

        level_rv = view.findViewById(R.id.level_rv);
        level_rv.setLayoutManager(linearLayoutManager);
        level_rv.setAdapter(adapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private ArrayList<LevelData> getArrayList() {
        ArrayList<LevelData> arrayList = new ArrayList<>();
        arrayList.add(new LevelData("Сканворд №1", "Тематика: Общая", 73, true, 0));
        arrayList.add(new LevelData("Сканворд №2", "Тематика: Общая", 15, true, 0));
        arrayList.add(new LevelData("Сканворд №3", "Тематика: Общая", 0, true, 0));
        arrayList.add(new LevelData("Сканворд №4", "Тематика: Общая", 0, false, 0));
        arrayList.add(new LevelData("Сканворд №5", "Тематика: Общая", 0, false, 0));
        return arrayList;
    }

    @Override
    public void onClick(View view) {
        getActivity().onBackPressed();
    }
}
