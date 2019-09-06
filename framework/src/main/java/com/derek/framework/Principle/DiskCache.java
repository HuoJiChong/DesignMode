package com.derek.framework.Principle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DiskCache implements ImageCache {
    static String cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/cache/";

    @Override
    public Bitmap get(String url) {
        return BitmapFactory.decodeFile(cacheDir + url);
    }

    @Override
    public void put(String url, Bitmap bitmap) {
        String path = imageUrl2MD5(url);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(cacheDir + path);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            CloseUtils.closeQuietly(outputStream);
        }
    }

    private String imageUrl2MD5(String url) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(url.getBytes());
            return new BigInteger(1, md.digest()).toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }


}
