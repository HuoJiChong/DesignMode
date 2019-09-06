package com.derek.framework.Builder;

import com.derek.framework.Builder.base.Builder;

/**
 * Director----统一组装过程
 */
public class Director {
    private Builder mBuilder = null;
    public Director(Builder builder){
        mBuilder = builder;
    }

    public void constructor(String display,String board){
        mBuilder.buildBoard(board);
        mBuilder.buildDisplay(display);
        mBuilder.buildOS();
    }
}
