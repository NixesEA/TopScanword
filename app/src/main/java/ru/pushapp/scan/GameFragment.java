package ru.pushapp.scan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GameFragment extends Fragment {

    GridAdapter adapter;
    GridView gridView;
    ArrayList<CellUnit> items = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.game_fragment, container, false);

        gridView = view.findViewById(R.id.game_gridview);

        return view;
    }

    @Override
    public void onStart() {

        //parse json with questions
        String myJson=inputStreamToString(getActivity().getResources().openRawResource(R.raw.question));
        objectJSON objectJSON = new Gson().fromJson(myJson, objectJSON.class);

        items.clear();
        for (int i = 0; i < objectJSON.rows.size(); i++){
            for (int j = 0; j < objectJSON.rows.get(i).cellsInRow.size(); j++){
                items.add(objectJSON.rows.get(i).cellsInRow.get(j));
            }
        }

        adapter = new GridAdapter(getContext(), items);
        gridView.setAdapter(adapter);


//        items.clear();


        //first raw
//        items.add(new GridItem("Плисецкая"));
//        items.add(new GridItem('Б'));
//        items.add(new GridItem('А'));
//        items.add(new GridItem('Л'));
//        items.add(new GridItem('Е'));
//        items.add(new GridItem('Р'));
//        items.add(new GridItem('И'));
//        items.add(new GridItem('Н'));
//        items.add(new GridItem('А'));
//
        //second raw
//        items.add(new GridItem("Привал"));
//        items.add(new GridItem('И'));
//        items.add(new GridItem("Радар"));
//        items.add(new GridItem('О'));
//        items.add(new GridItem("... - дело благородное"));
//        items.add(new GridItem('И'));
//        items.add(new GridItem("Револьвер"));
//        items.add(new GridItem('А'));
//        items.add(new GridItem("Рог (греч.)"));

        //third raw
//        items.add(new GridItem("100 лет"));
//        items.add(new GridItem('В'));
//        items.add(new GridItem('Е'));
//        items.add(new GridItem('К'));
//        items.add(new GridItem("... - о Форсайтах"));
//        items.add(new GridItem('С'));
//        items.add(new GridItem('А'));
//        items.add(new GridItem('Г'));
//        items.add(new GridItem('А'));
//
//        4 raw
//        items.add(new GridItem("Морская рыба"));
//        items.add(new GridItem('А'));
//        items.add(new GridItem("Агропромышленный комплекс"));
//        items.add(new GridItem('А'));
//        items.add(new GridItem('П'));
//        items.add(new GridItem('К'));
//        items.add(new GridItem("Марка самолета"));
//        items.add(new GridItem('А'));
//        items.add(new GridItem('Н'));
//
        //5 raw
//        items.add(new GridItem('С'));
//        items.add(new GridItem('К'));
//        items.add(new GridItem('А'));
//        items.add(new GridItem('Т'));
//        items.add(new GridItem("Самка павлина"));
//        items.add(new GridItem("Река в Индии"));
//        items.add(new GridItem('И'));
//        items.add(new GridItem('Н'));
//        items.add(new GridItem('Д'));
//
//        6 raw
//        items.add(new GridItem("Украинские сигареты"));
//        items.add(new GridItem("Часть зарплаты"));
//        items.add(new GridItem("Партия товара"));
//        items.add(new GridItem('О'));
//        items.add(new GridItem('П'));
//        items.add(new GridItem('Т'));
//        items.add(new GridItem("Войсковое соединение"));
//        items.add(new GridItem("Длиннохвостый попугай"));
//        items.add(new GridItem('О'));
//
        //7 raw
//        items.add(new GridItem('В'));
//        items.add(new GridItem('А'));
//        items.add(new GridItem('Т'));
//        items.add(new GridItem('Р'));
//        items.add(new GridItem('А'));
//        items.add(new GridItem("Незасеянное поле"));
//        items.add(new GridItem('П'));
//        items.add(new GridItem('А'));
//        items.add(new GridItem('Р'));
//
        //8 raw
//        items.add(new GridItem("Почтовая ..."));
//        items.add(new GridItem('В'));
//        items.add(new GridItem("Статор"));
//        items.add(new GridItem("Взгляд"));
//        items.add(new GridItem('В'));
//        items.add(new GridItem('З'));
//        items.add(new GridItem('О'));
//        items.add(new GridItem('Р'));
//        items.add(new GridItem("Английская принцесса"));
//
        //9 raw
//        items.add(new GridItem('М'));
//        items.add(new GridItem('А'));
//        items.add(new GridItem('Р'));
//        items.add(new GridItem('К'));
//        items.add(new GridItem('А'));
//        items.add(new GridItem("Минор"));
//        items.add(new GridItem('Л'));
//        items.add(new GridItem('А'));
//        items.add(new GridItem('Д'));
//
//
//        open keyboard
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);


        super.onStart();
    }

    public String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }

}
