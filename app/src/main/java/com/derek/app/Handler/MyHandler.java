package com.derek.app.Handler;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class MyHandler extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    Handler myHandler = new Handler(Looper.getMainLooper(),new MyCallback());

    public void test(){

    }

    class MyCallback implements Handler.Callback {
        @Override
        public boolean handleMessage(Message msg) {

            return true;
        }
    }
}
