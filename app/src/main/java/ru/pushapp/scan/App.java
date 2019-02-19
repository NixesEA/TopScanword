package ru.pushapp.scan;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import ru.pushapp.scan.Adapters.LevelData;
import ru.pushapp.scan.JsonUtil.ObjectJSON;
import ru.pushapp.scan.JsonUtil.RowUnit;

public class App extends Application {

    static ArrayList<ObjectJSON> crosswordList;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static ArrayList<ObjectJSON> initCrosswordList(Context context) {
        crosswordList = new ArrayList<>();

        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            String json = inputStreamToString(context.getResources().openRawResource(
                    context.getResources().getIdentifier(field.getName(), "raw", context.getPackageName())));
            ObjectJSON objectJSON = new Gson().fromJson(json, ObjectJSON.class);

            crosswordList.add(objectJSON);
        }
        return crosswordList;
    }

    public static ArrayList<RowUnit> getScanword(int index){
        return crosswordList.get(index).rows;
    }

    public static LevelData getCrosswordInfo(int index){
        String title = "Сканворд №" + crosswordList.get(index).number;
        String descriptions = "Тематика: " + crosswordList.get(index).theme;
        String res = "scanword_" + (index + 1);
        int progress = crosswordList.get(index).progress;
        boolean unblock = crosswordList.get(index).unblocked;

        return new LevelData(title, descriptions, progress, unblock, res);
    }

    public static int getCrosswordSize(){
        return crosswordList.size();
    }

    static public String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            return new String(bytes);
        } catch (IOException e) {
            return null;
        }
    }
}
