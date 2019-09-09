package com.derek.framework.Prototype;

import java.util.ArrayList;

/**
 * Cloneable 是一个标识接口，标志这个这个类是可拷贝的，
 * clone()方法是重写的Object方法。
 */
public class WordDocument implements Cloneable {
    private String mText;
    private ArrayList<String> mImages = new ArrayList<>();

    public WordDocument() {
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public ArrayList<String> getmImages() {
        return mImages;
    }

    public void setmImages(ArrayList<String> mImages) {
        this.mImages = mImages;
    }

    @Override
    protected WordDocument clone()  {
        try{
            WordDocument doc = (WordDocument) super.clone();
            doc.mText = this.mText;
            doc.mImages = (ArrayList<String>) this.mImages.clone();
            return doc;
        }catch ( CloneNotSupportedException e){
            e.printStackTrace();
        }
        return null;
    }

    public void showDocment(){
        System.out.println("------------------ >>>>>>>>>>>>>>>>>>>> ------------------");
        System.out.println("------------------ Text: " + mText + " ------------------");
        for (int i = 0;i<mImages.size();i++) {
            System.out.println("------------------ Image: " + mImages.get(i) + " ------------------");
        }
        System.out.println("------------------ >>>>>>>>>>>>>>>>>>>> ------------------");
    }
}
