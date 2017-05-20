package com.smie.hackthemoney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by hp on 2017/5/20.
 */
public class Dummy extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Intent i = new Intent(this, MyAccessibilityService.class);
        //startService(i);
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 0);
    }
}
