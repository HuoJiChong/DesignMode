package com.derek.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.derek.app.Memo.NoteCaretaker;
import com.derek.app.binder.BankService;
import com.derek.eventbus.EventBus;
import com.derek.eventbus.annotation.Subscriber;

public class MainActivity extends Activity {

    private IBankAIDL mBankBinder;

    NoteCaretaker caretaker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        EventBus.getDefault().register(this);

        bindService(new Intent(this,BankService.class),conn,BIND_AUTO_CREATE);

        caretaker = new NoteCaretaker();
    }

    static int a = 0;
    public void onClick(View view) {
        int id  = view.getId();
        switch (id){
            case R.id.btn_broad:
            {
                EventBus.getDefault().post(" hello ");
            }
                break;
            case R.id.btn_query:
            {
                EventBus.getDefault().post(10);
            }
                break;
            case R.id.btn_open:
            {
                try {
                    Log.e("derek",mBankBinder.openAccount("aigo","1234"));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
                break;
            default:
                break;
        }
    }

    @Subscriber
    public void sayHello(String name){
        a++;
        Log.e("sayHello",name +" "+ a);
    }

    @Subscriber
    public void add(String name){
        a++;
        Log.e("add",name +" "+ a);
    }

    @Subscriber
    public void sub(int num){
        a++;
        Log.e("sub",num +" "+ a);
    }

    public void onNoteClick(View v){

    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBankBinder = IBankAIDL.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context,"update",Toast.LENGTH_SHORT).show();
//        }
//    };




    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
    }
}
