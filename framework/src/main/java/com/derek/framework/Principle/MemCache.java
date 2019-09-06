package com.derek.framework.Principle;

import android.graphics.Bitmap;
import android.util.LruCache;

public class MemCache implements ImageCache {

    LruCache<String, Bitmap> cache ;

    public MemCache(){
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxSize / 4;

        cache = new LruCache<String, Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getWidth() * value.getHeight() / 1024;
            }
        };
    }

    @Override
    public Bitmap get(String url) {
        return cache.get(url);
    }

    @Override
    public void put(String url,Bitmap bitmap){
        cache.put(url,bitmap);
    }

}
