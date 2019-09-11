package com.derek.framework.Handler;

import com.derek.framework.Handler.base.AbstractHandler;
import com.derek.framework.Handler.base.AbstractRequest;

public class Handler2 extends AbstractHandler {
    @Override
    protected int getHandleLevel() {
        return 2;
    }

    @Override
    protected void handle(AbstractRequest request) {
        System.out.println("Handler2 handle request:" + request.getRequestLevel());
    }
}
