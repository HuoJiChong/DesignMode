package com.derek.framework.Handler;

import com.derek.framework.Handler.base.AbstractHandler;
import com.derek.framework.Handler.base.AbstractRequest;

public class Handler3 extends AbstractHandler {
    @Override
    protected int getHandleLevel() {
        return 3;
    }

    @Override
    protected void handle(AbstractRequest request) {
        System.out.println("Handler3 handle request:" + request.getRequestLevel());
    }
}
