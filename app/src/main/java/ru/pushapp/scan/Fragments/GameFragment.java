package ru.pushapp.scan.Fragments;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import ru.pushapp.scan.Adapters.GridAdapter;
import ru.pushapp.scan.CustomGameTable;
import ru.pushapp.scan.JsonUtil.CellUnit;
import ru.pushapp.scan.JsonUtil.ObjectJSON;
import ru.pushapp.scan.R;

public class GameFragment extends Fragment {

//    GridAdapter adapter;
//    GridView gridView;
    ArrayList<CellUnit> items = new ArrayList<>();

    CustomGameTable customGameTable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

//        gridView = view.findViewById(R.id.game_grid_view);
        customGameTable = view.findViewById(R.id.customPanel);
//        customGameTable.setLayoutParams(new ConstraintLayout.LayoutParams(900,1200));

        // Create the Keyboard
//        Keyboard mKeyboard = new Keyboard(getActivity(), R.xml.number_pad);
        // Lookup the KeyboardView
//        KeyboardView mKeyboardView = view.findViewById(R.id.keyboardview);
        // Attach the keyboard to the view
//        mKeyboardView.setKeyboard(mKeyboard);
//        mKeyboardView.setPreviewEnabled(false);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return view;
    }

    @Override
    public void onResume() {
        //parse questions-json
        String myJson = inputStreamToString(getActivity().getResources().openRawResource(R.raw.question));
        ObjectJSON objectJSON = new Gson().fromJson(myJson, ObjectJSON.class);

        if (items.size() == 0) {
            for (int i = 0; i < objectJSON.rows.size(); i++) {
                items.addAll(objectJSON.rows.get(i).cellsInRow);
            }
        }
        customGameTable.setContent(objectJSON.rows);

        super.onResume();
    }

    public String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }

}
