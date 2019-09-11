package com.derek.framework.Handler.base;

public abstract class AbstractHandler {
    public AbstractHandler nextHandler;

    public final void handleRequest(AbstractRequest request){
        if (getHandleLevel() == request.getRequestLevel()){
            handle(request);
        }else{
            if (nextHandler != null){
                nextHandler.handleRequest(request);
            }else{
                System.out.println(" All of handler can not handle the request." + request.toString() );
            }
        }
    }

    protected abstract int getHandleLevel();

    protected abstract void handle(AbstractRequest request);
}
