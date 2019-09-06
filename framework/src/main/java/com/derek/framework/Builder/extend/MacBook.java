package com.derek.framework.Builder.extend;

import com.derek.framework.Builder.base.Computer;

/**
 * 具体的产品类
 */
public class MacBook extends Computer {

    protected MacBook(){

    }

    @Override
    public void setOS() {
        mOS = "MAC OS X 14.6";
    }
}
