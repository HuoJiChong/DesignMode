package com.derek.framework.Builder.base;

/**
 * Builder----抽象Builder类
 */
public abstract class Builder {
    public abstract void buildBoard(String board);
    public abstract void buildDisplay(String display);
    public abstract void buildOS();

    public abstract Computer create();
}
