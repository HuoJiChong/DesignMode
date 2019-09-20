package com.derek.app.binder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BankService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return new BankBinder();
    }
}
