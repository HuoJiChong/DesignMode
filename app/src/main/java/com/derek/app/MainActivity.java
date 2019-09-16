package com.derek.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import com.derek.app.Memo.NoteCaretaker;
import com.derek.app.Memo.NoteEditText;
import com.derek.eventbus.EventBus;
import com.derek.eventbus.annotation.Subscriber;

public class MainActivity extends FragmentActivity {

    NoteEditText et;
    NoteCaretaker caretaker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_note);

//        EventBus.getDefault().register(this);

        caretaker = new NoteCaretaker();
        et = findViewById(R.id.edit_text);
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
        int id = v.getId();
        switch (id){
            case R.id.btn_note_pre:
            {
                et.restore(caretaker.getPreMemo());
            }
                break;
            case R.id.btn_note_save:
            {
                caretaker.saveMemo(et.createMemo());
            }
                break;
            case R.id.btn_note_next:
            {
                et.restore(caretaker.getNextMemo());
            }
                break;
        }
    }


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
