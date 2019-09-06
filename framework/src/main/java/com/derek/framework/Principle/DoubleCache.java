package com.derek.framework.Principle;

import android.graphics.Bitmap;

public class DoubleCache implements ImageCache {

    MemCache memCache = new MemCache();
    DiskCache diskCache = new DiskCache();

    @Override
    public Bitmap get(String url) {
        Bitmap bmp = memCache.get(url);
        if (bmp == null){
            bmp = diskCache.get(url);
        }
        return bmp;
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        memCache.put(url,bitmap);
        diskCache.put(url,bitmap);
    }
}
