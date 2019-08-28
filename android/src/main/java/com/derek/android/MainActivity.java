package com.derek.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.derek.aop.annotation.BehaviorTrace;
import com.derek.aop.annotation.DebugTrace;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @BehaviorTrace(value = "click",type = 1)
    public void onClick(View v){
        Log.i("derek","  click   ");
    }

    @DebugTrace
    public void onShake(View v){

    }
}
