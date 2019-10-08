package com.derek.app;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.derek.app.Memo.NoteCaretaker;
import com.derek.app.binder.BankService;
import com.derek.eventbus.EventBus;
import com.derek.eventbus.annotation.Subscriber;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    private IBankAIDL mBankBinder;

    NoteCaretaker caretaker;

    private static final String dbPwd = "123456";
    private static final String dbName = "teacher.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindService(new Intent(this,BankService.class),conn,BIND_AUTO_CREATE);

        caretaker = new NoteCaretaker();

        // 数据库测试
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPer() {
        String[] per = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(checkSelfPermission(per[0]) == PackageManager.PERMISSION_DENIED){
            requestPermissions(per,200);
            return false;
        }else{
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean granted = true;
        for (int i = 0;i<grantResults.length;i++){
             if(checkSelfPermission(permissions[i]) == PackageManager.PERMISSION_DENIED){
                 granted = false;
                 break;
             }
        }
        if (granted){
        }else {
            checkPer();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            case R.id.btn_insert:
            {
                for (int i = 1;i<6;i++)
                {
                }
            }
                break;

            case R.id.btn_update:
            {
            }
                break;

            case R.id.btn_delete:
            {
            }
                break;

            case R.id.btn_log:
            {
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
        unbindService(conn);
    }
}
