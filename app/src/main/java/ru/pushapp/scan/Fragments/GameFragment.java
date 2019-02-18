package ru.pushapp.scan.Fragments;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ru.pushapp.scan.Adapters.GridAdapter;
import ru.pushapp.scan.CustomGameTable;
import ru.pushapp.scan.JsonUtil.CellUnit;
import ru.pushapp.scan.JsonUtil.ObjectJSON;
import ru.pushapp.scan.MainActivity;
import ru.pushapp.scan.MyInputMethodService;
import ru.pushapp.scan.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class GameFragment extends Fragment implements KeyboardView.OnKeyboardActionListener {

    ArrayList<CellUnit> items = new ArrayList<>();

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

    @Override
    public void onPress(int i) {
        if (i == -5){
            customGameTable.deleteLetter();
            return;
        }

        StringBuilder mComposing = new StringBuilder();

        String value =   mComposing.append((char) i).toString();
        Log.i("keyboardTEST","onText " + value);

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
