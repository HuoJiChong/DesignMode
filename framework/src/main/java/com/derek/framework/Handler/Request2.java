package com.derek.framework.Handler;

import com.derek.framework.Handler.base.AbstractRequest;

public class Request2 extends AbstractRequest {
    public Request2(Object obj) {
        super(obj);
    }

    @Override
    public int getRequestLevel() {
        return 2;
    }
}
