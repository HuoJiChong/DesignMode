package com.derek.app.binder;

import android.os.RemoteException;

import com.derek.app.IBankAIDL;

import java.util.UUID;

public class BankBinder extends IBankAIDL.Stub {

    @Override
    public String openAccount(String name, String pwd) throws RemoteException {
        return name + "开户成功! 账户为：" + UUID.randomUUID().toString();
    }

    @Override
    public String saveMoney(int money, String account) throws RemoteException {
        return "账户：" + account + " 存入" + money + "单位：人民币";
    }

    @Override
    public String takeMoney(int money, String account, String pwd) throws RemoteException {
        return "账户：" + account + " 支取" + money + "单位：人民币";
    }

    @Override
    public String closeAccount(String name, String pwd) throws RemoteException {
        return name + "退出成功。";
    }
}
