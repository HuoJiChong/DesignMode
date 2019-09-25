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
import com.derek.app.db.User;
import com.derek.app.db.UserDao;
import com.derek.db.DaoFactory;
import com.derek.db.IBaseDao;
import com.derek.eventbus.EventBus;
import com.derek.eventbus.annotation.Subscriber;

import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    private IBankAIDL mBankBinder;

    NoteCaretaker caretaker;

    IBaseDao<User> userDao;
    private static final String dbPwd = "123456";
    private static final String dbName = "teacher.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        EventBus.getDefault().register(this);

        bindService(new Intent(this,BankService.class),conn,BIND_AUTO_CREATE);

        caretaker = new NoteCaretaker();

        // 数据库测试
        DaoFactory.getInstance().init(getApplicationContext(),dbPwd,dbName);
        userDao = DaoFactory.getInstance().getDataHelper(UserDao.class,User.class);
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
                User where=new User();
                where.setName("teacher");
                where.setUser_Id(10);
                List<User> list=userDao.query(where);

                Log.i(TAG,"查询到  "+ ((list == null) ? "0" : list.size() )+"  条数据");

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
                for (int i = 0;i<10;i++)
                {
                    User user=new User(i,"teacher","123456");
                    userDao.insert(user);
                }
            }
                break;

            case R.id.btn_update:
            {
                User user=new User(10,"teacher","123456");
//                userDao.insert(user);
//                userDao.delete(user);
                User entity = new User(100,"derek","check");

                userDao.update(entity,user);
            }
                break;

            case R.id.btn_delete:
            {
                User user=new User(10,"teacher","123456");
//                userDao.insert(user);
                userDao.delete(user);
            }
                break;

            case R.id.btn_log:
            {
                User where = new User();
                List<User> list=userDao.query(where);
                for (int i = 0;i<list.size();i++){
                    Log.e("derek", list.get(i).toString());
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
        unbindService(conn);
    }
}
