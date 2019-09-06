package com.derek.framework.Builder.extend;

import com.derek.framework.Builder.base.Builder;
import com.derek.framework.Builder.base.Computer;

/**
 * ConcreteBuilder----具体的Builder类
 */
public class MacBookBuilder extends Builder {

    private MacBook macBook = new MacBook();

    @Override
    public void buildBoard(String board) {
        macBook.setBoard(board);
    }

    @Override
    public void buildDisplay(String display) {
        macBook.setDisplay(display);
    }

    @Override
    public void buildOS() {
        macBook.setOS();
    }

    @Override
    public Computer create() {
        return macBook;
    }
}
