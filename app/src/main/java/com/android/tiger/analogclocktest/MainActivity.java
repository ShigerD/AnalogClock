package com.android.tiger.analogclocktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
private final String TAG=MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w(TAG,"1+onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_analogclock);
    }
}
