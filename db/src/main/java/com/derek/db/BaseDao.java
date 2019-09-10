package com.derek.db;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.derek.db.annotation.DbTable;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public abstract class BaseDao<T> implements IBaseDao<T> {

    /**
     * 数据库的引用
     */
    private SQLiteDatabase database;
    /**
     * 保证实例化一次
     */
    private boolean isInit=false;
    /**
     * 持有操作数据库表所对应的java类型
     * User
     */
    private Class<T> entityClass;
    /**
     * 维护这表名与成员变量名的映射关系
     * key---》表名
     * value --》Field
     * class  methoFiled
     * {
     *     Method  setMthod
     *     Filed  fild
     * }
     */
    private HashMap<String,Field> cacheMap;

    private String tableName;

    protected synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if(!isInit) {
            entityClass=entity;
            database=sqLiteDatabase;
            if (entity.getAnnotation(DbTable.class)==null) {
                tableName=entity.getClass().getSimpleName();
            }else {
                tableName=entity.getAnnotation(DbTable.class).value();
            }
            if(!database.isOpen()) {
                return  false;
            }
            if(!TextUtils.isEmpty(createTable())) {
                database.execSQL(createTable());
            }
            cacheMap=new HashMap<>();
            initCacheMap();

            isInit=true;
        }
        return  isInit;
    }

    private void initCacheMap(){

    }

    @Override
    public Long insert(T entity) {
        return null;
    }

    @Override
    public int update(T entity, T where) {
        return 0;
    }

    @Override
    public int delete(T where) {
        return 0;
    }

    @Override
    public List<T> query(T where) {
        return null;
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        return null;
    }

    public abstract String createTable();
}
