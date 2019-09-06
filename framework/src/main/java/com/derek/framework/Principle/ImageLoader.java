package com.derek.framework.Principle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private Handler handler = new Handler(Looper.getMainLooper());

    private ExecutorService  executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // 图片缓存类，依赖于抽象，并且有一个默认的实现。
    private ImageCache mCache = new MemCache();

    private ImageLoaderConfig config;

    private ImageLoaderConfig.DisplayConfig displayConfig = new ImageLoaderConfig.DisplayConfig();

    private ImageLoader(){

    }

    private static class ImageLoaderHolder{
        private static final ImageLoader instance = new ImageLoader();
    }

    public static ImageLoader getSingle(){
        return ImageLoaderHolder.instance;
    }

    public ImageLoader init(ImageLoaderConfig config){
        this.config = config;
        checkConfig();
        return ImageLoaderHolder.instance;
    }

    private void checkConfig() {
        if (this.config.cache != null){
            mCache = this.config.cache;
        }
        if (config.threadCount != Runtime.getRuntime().availableProcessors()){
            executorService.shutdown();
            executorService = null;
            executorService = Executors.newFixedThreadPool(config.threadCount);
        }
        if (config.displayConfig != null){
            displayConfig = config.displayConfig;
        }
    }

    public void displayImage(final String url, final ImageView imageView){
        Bitmap bitmap = null;
        if (mCache != null) {
            bitmap = mCache.get(url);
        }

        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
            return;
        }

        downloadImageAsync(url,imageView);
    }

    private void downloadImageAsync(final String url, final ImageView imageView){
        imageView.setTag(url);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(url);
                if (bitmap == null){
                    return;
                }
                if (imageView.getTag().equals(url)){
                    updateImageView(bitmap,imageView);
                }
                mCache.put(url,bitmap);
            }
        });
    }

    /**
     * 下载图片
     * @param imageUrl
     * @return
     */
    private Bitmap downloadImage(String imageUrl){
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 更新图片
     * @param bitmap
     * @param imageView
     */
    private void updateImageView(final Bitmap bitmap, final ImageView imageView){
        handler.post(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
            }
        });
    }


}
