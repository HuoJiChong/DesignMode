package com.derek.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

public class DaoFactory {
    private static class DaoFactoryHolder {
        private static DaoFactory instance = new DaoFactory();
    }

    private boolean inited = false;
    private String databasePath = "default.db";
    private SQLiteDatabase sqLiteDatabase;

    private DaoFactory(){

    }

    public void init(String databaseName){
        databasePath = databaseName;
        openDatabase();
        inited = true;
    }

    private void openDatabase(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + databasePath;
        sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(path,null);
    }

    public static DaoFactory getInstance(){
        return DaoFactoryHolder.instance;
    }

    public synchronized <T extends BaseDao<M>,M > T getDataHelper(Class<T> clazz,Class<M> entity){
        if (!inited){
            try {
                throw new Exception("");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        BaseDao baseDao=null;
        try {
            baseDao=clazz.newInstance();
            baseDao.init(entity,sqLiteDatabase);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T) baseDao;
    }
}
