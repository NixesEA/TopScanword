package ru.pushapp.scan;

import android.app.Application;
import android.util.Log;

import java.lang.reflect.Field;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void getAllRaw() {
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            Log.i("TESTRAW", "test raw = " + field.getName() + " " + field.getModifiers());
        }
    }
}
