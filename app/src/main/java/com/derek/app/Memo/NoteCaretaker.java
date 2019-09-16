package com.derek.app.Memo;

import java.util.ArrayList;

public class NoteCaretaker {
    private static final int MAX = 30;
    private ArrayList<Memo> memos = new ArrayList<Memo>(MAX){
        @Override
        protected void finalize() throws Throwable {
            super.finalize();
        }
    };

    private int mIndex = 0;

    public void saveMemo(Memo memo){
        if (memos.size() > MAX){
            memos.remove(0);
        }
        memos.add(memo);
        mIndex = memos.size() - 1;
    }

    public Memo getPreMemo(){
        if (memos.size() == 0){
            return null;
        }
        mIndex = mIndex > 0 ? --mIndex : mIndex;
        return memos.get(mIndex);
    }

    public Memo getNextMemo(){
        if (memos.size() == 0){
            return null;
        }
        mIndex = mIndex < memos.size() - 1 ? ++mIndex:mIndex;
        return memos.get(mIndex);
    }
}
