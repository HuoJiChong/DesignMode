package com.derek.framework.Observer;

import java.util.Observable;

/**
 * 被观察者
 */
public class DevTechFrontier extends Observable {

    public void postNewPublication(String content){
        setChanged();
        notifyObservers(content);
    }
}
