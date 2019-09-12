package com.derek.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }


//    public void onClick(View view) {
//        int id  = view.getId();
//        switch (id){
//            case R.id.btn_broad:
//            {
//                Intent broad = new Intent("info.update");
//                sendBroadcast(broad);
//            }
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        IntentFilter filter = new IntentFilter("info.update");
//        registerReceiver(receiver,filter);
//    }
//
//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context,"update",Toast.LENGTH_SHORT).show();
//        }
//    };
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        unregisterReceiver(receiver);
//    }

}
