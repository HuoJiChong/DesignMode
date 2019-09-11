package com.derek.framework.Handler.base;

public abstract class AbstractRequest {
    private Object obj;

    public AbstractRequest(Object obj) {
        this.obj = obj;
    }

    /**
     * 获取具体内容对象
     * @return
     */
    public Object getContent() {
        return obj;
    }

    /**
     * 获取请求级别
     * @return
     */
    public abstract int getRequestLevel();

    @Override
    public String toString() {
        return "AbstractRequest{" +
                "obj=" + obj +
                '}';
    }
}
