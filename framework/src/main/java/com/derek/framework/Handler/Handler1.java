package com.derek.framework.Handler;

import com.derek.framework.Handler.base.AbstractHandler;
import com.derek.framework.Handler.base.AbstractRequest;

public class Handler1 extends AbstractHandler {
    @Override
    protected int getHandleLevel() {
        return 1;
    }

    @Override
    protected void handle(AbstractRequest request) {
        System.out.println("Handler1 handle request:" + request.getRequestLevel());
    }
}
