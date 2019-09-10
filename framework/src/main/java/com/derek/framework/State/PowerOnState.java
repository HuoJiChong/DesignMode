package com.derek.framework.State;

public class PowerOnState implements TvState {
    @Override
    public void nextChannel() {
        System.out.println("nextChannel");
    }

    @Override
    public void prevChannel() {
        System.out.println("prevChannel");
    }

    @Override
    public void turnUp() {
        System.out.println("turnUp");
    }

    @Override
    public void turnDown() {
        System.out.println("turnDown");
    }
}
