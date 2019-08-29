package com.derek.android;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.derek.android.aop.annotation.BehaviorTrace;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @BehaviorTrace(value = "mShake",type = 1)
    public  void mShake(View view)
    {
        SystemClock.sleep(3000);
        Log.i("derek","  mShake   ");
    }

    public void onAopTest(View v){
        Log.i("derek","  onAopTest   ");
    }

}
