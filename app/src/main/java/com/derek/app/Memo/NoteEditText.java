package com.derek.app.Memo;

import android.content.Context;
import android.util.AttributeSet;

public class NoteEditText extends android.support.v7.widget.AppCompatEditText {
    public NoteEditText(Context context) {
        super(context);
    }

    public NoteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Memo createMemo(){
        Memo memo = new Memo();
        memo.text = getText().toString();
        memo.cursour = getSelectionStart();
        return memo;
    }

    public void restore(Memo memo){
        if (memo == null){
            return;
        }
        setText(memo.text);
        setSelection(memo.cursour);
    }
}
