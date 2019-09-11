package com.derek.framework.Handler;

import com.derek.framework.Handler.base.AbstractRequest;

public class Request1 extends AbstractRequest {
    public Request1(Object obj) {
        super(obj);
    }

    @Override
    public int getRequestLevel() {
        return 1;
    }
}
