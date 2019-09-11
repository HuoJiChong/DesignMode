package com.derek.framework.Handler;

import com.derek.framework.Handler.base.AbstractRequest;

public class Request3 extends AbstractRequest {
    public Request3(Object obj) {
        super(obj);
    }

    @Override
    public int getRequestLevel() {
        return 3;
    }
}
