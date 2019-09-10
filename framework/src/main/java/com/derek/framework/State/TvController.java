package com.derek.framework.State;

public class TvController implements PowerController {
    TvState state ;

    private void setState(TvState state) {
        this.state = state;
    }

    @Override
    public void powerOff() {
        setState(new PowerOffState());
        System.out.println("Power Off");
    }

    @Override
    public void powerOn() {
        setState(new PowerOnState());
    }

    public void nextChannel() {
        state.nextChannel();
    }

    public void prevChannel() {
        state.prevChannel();
    }

    public void turnUp() {
        state.turnUp();
    }

    public void turnDown() {
        state.turnDown();
    }
}
