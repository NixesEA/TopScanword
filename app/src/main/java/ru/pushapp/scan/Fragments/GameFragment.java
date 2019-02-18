package ru.pushapp.scan.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.pushapp.scan.CustomGameTable;
import ru.pushapp.scan.JsonUtil.CellUnit;
import ru.pushapp.scan.JsonUtil.ObjectJSON;
import ru.pushapp.scan.JsonUtil.RowUnit;
import ru.pushapp.scan.R;

public class GameFragment extends Fragment implements KeyboardView.OnKeyboardActionListener {

    ArrayList<CellUnit> items = new ArrayList<>();

    String FILE_NAME = "";

    Keyboard mKeyboard;
    CustomGameTable customGameTable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

        // Create the Keyboard
        mKeyboard = new Keyboard(getActivity(), R.xml.number_pad);
        KeyboardView mKeyboardView = view.findViewById(R.id.keyboardview);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(this);
        mKeyboardView.setPreviewEnabled(false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        customGameTable = view.findViewById(R.id.customPanel);
        FILE_NAME = getArguments().getString("id_scanword");

        return view;
    }

    @Override
    public void onResume() {
        String json;
        ObjectJSON objectJSON = new ObjectJSON();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS);
        json = sharedPreferences.getString("content", null);
        if (json == null){
            json = inputStreamToString(getActivity().getResources().openRawResource(
                    getResources().getIdentifier(FILE_NAME,"raw", getContext().getPackageName())));

            objectJSON = new Gson().fromJson(json, ObjectJSON.class);
        } else {
            Type type = new TypeToken<ArrayList<RowUnit>>() {}.getType();
            objectJSON.rows = new Gson().fromJson(json, type);
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

    @Override
    public void onPause() {
        super.onPause();

        float progress = customGameTable.saveProgress();
        ArrayList<RowUnit> arrayList = customGameTable.saveData();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(FILE_NAME, Context.MODE_MULTI_PROCESS);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("content", json);
        editor.putFloat("progress", progress);
        editor.commit();

    }

    @Override
    public void onPress(int i) {
        if (i == -5) {
            customGameTable.deleteLetter();
            return;
        }

        StringBuilder mComposing = new StringBuilder();
        String value = mComposing.append((char) i).toString();

        customGameTable.setLetter(value);
    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int i, int[] ints) {

    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
