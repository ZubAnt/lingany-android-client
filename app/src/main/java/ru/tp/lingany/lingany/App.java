package ru.tp.lingany.lingany;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.gsonparserfactory.GsonParserFactory;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("App", "onCreate");
        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new GsonParserFactory());
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
