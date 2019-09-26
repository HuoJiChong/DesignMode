package com.derek.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.derek.db.annotation.DbFiled;
import com.derek.db.annotation.DbTable;

//import net.sqlcipher.Cursor;
//import net.sqlcipher.database.SQLiteDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseDao<T> implements IBaseDao<T> {

    /**
     * 数据库的引用
     */
    protected SQLiteDatabase database;
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

    protected String tableName;

    protected synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase) {
        if(!isInit) {
            entityClass=entity;
            database=sqLiteDatabase;

            DbTable dbTable = entity.getAnnotation(DbTable.class);
            if (dbTable==null) {
                tableName=entity.getClass().getSimpleName();
            }else {
                tableName=dbTable.value();
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
        /**
         * 第一条数据，查0个数据
         */
        String sql = "select * from " + this.tableName + " limit 1,0";
        Cursor cursor ;

        cursor = database.rawQuery(sql,null);
        // 获取列名数组
        String[] columnNames = cursor.getColumnNames();
        // 获取Field数组
        Field[] columnFields = entityClass.getFields();
        //设置访问权限
        for (Field field : columnFields){
            field.setAccessible(true);
        }
        try{
            // 开始对应，数据库列名和实体类对应
            for (String columnName : columnNames ){
                Field columnField = null;
                for (Field field : columnFields){
                    String fieldName = null;
                    DbFiled dfFieldAnno = field.getAnnotation(DbFiled.class);
                    if (dfFieldAnno!= null){
                        fieldName =  dfFieldAnno.value();
                    }else {
                        fieldName = field.getName();
                    }

                    if (columnName.equals(fieldName)){
                        columnField = field;
                        break;
                    }
                }
                // 找到了对应关系
                if (columnField != null){
                    cacheMap.put(columnName,columnField);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cursor.close();
        }

    }

    @Override
    public Long insert(T entity) {
        Map<String,String> map = getValues(entity);
        ContentValues values = getContentValues(map);
        long result = database.insert(this.tableName,null,values);
        return result;
    }

    @Override
    public int update(T entity, T where) {
        Map entityMap = getValues(entity);
        ContentValues entityValues = getContentValues(entityMap);

        DaoCondition condition = new DaoCondition(getValues(where));

        int result = database.update(this.tableName,entityValues,condition.getWhereClause(),condition.getWhereArgs());
        return result;
    }

    @Override
    public int delete(T where) {
        Map<String,String> map = getValues(where);
        DaoCondition condition = new DaoCondition(map);
        int result = database.delete(this.tableName,condition.getWhereClause(),condition.getWhereArgs());
        return result;
    }

    @Override
    public List<T> query(T where) {
        return query(where,null,null,null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map<String,String> map=getValues(where);

        String limitString=null;
        if(startIndex!=null&&limit!=null) {
            limitString=startIndex+" , "+limit;
        }

        DaoCondition condition=new DaoCondition(map);
        Log.e("derek",condition.getWhereClause());
//        Log.e("derek",condition.getWhereArgs());
        Cursor cursor=database.query(tableName,null,condition.getWhereClause()
                ,condition.getWhereArgs(),null,null,orderBy,limitString);
        List<T> result=getResult(cursor,where);
        cursor.close();
        return result;
    }

    /**
     * 将成员变量转换成
     *  -->> 表的列名，成员变量的值
     * @param entity 成员变量
     * @return
     */
    private Map<String,String> getValues(T entity){
        HashMap<String,String> result=new HashMap<>();
        Iterator<Field> filedsIterator=cacheMap.values().iterator();
        /**
         * 循环遍历 映射map的  Filed
         */
        while (filedsIterator.hasNext())
        {
            Field colmunToFiled=filedsIterator.next();
            String cacheKey;
            String cacheValue=null;
            if(colmunToFiled.getAnnotation(DbFiled.class)!=null) {
                cacheKey=colmunToFiled.getAnnotation(DbFiled.class).value();
            }else {
                cacheKey=colmunToFiled.getName();
            }
            try {
                /**
                 * 如果没有赋值，就过滤；
                 */
                Object value = colmunToFiled.get(entity);
                if( null == value ) {
                    continue;
                }
                try{
                    /**
                     * 过滤int 默认值
                     * 如果我想查找 "name == teacher"的数据类型，但是不知道id(int 类型)是多少，此时id默认值，就会是0，
                     * 条件就变成了 "name=teacher && id =0"
                     */
                    Type valueType = value.getClass().getGenericSuperclass();
                    if (valueType.equals(Number.class)){
                        int value2 = Integer.parseInt(value.toString());
                        if (value2 == 0){
                            continue;
                        }
                    }

                }catch (Exception e){

                }

                cacheValue=colmunToFiled.get(entity).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            result.put(cacheKey,cacheValue);
        }

        return result;
    }

    /**
     * 将map转换成 ContentValues
     * @param map map
     * @return
     */
    private ContentValues getContentValues(Map<String,String> map){
        ContentValues contentValues = new ContentValues();
        Set keys = map.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()){
            String key = (String) iterator.next();
            String value = map.get(key);

            if (value != null){
                contentValues.put(key,value);
            }
        }
        return contentValues;
    }

    protected List<T> getResult(Cursor cursor, T where) {
        ArrayList list=new ArrayList();

        Object item;
        while (cursor.moveToNext())
        {
            try {
                item=where.getClass().newInstance();
                /**
                 * 列名  name
                 * 成员变量名  Filed;
                 */
                Iterator iterator=cacheMap.entrySet().iterator();
                while (iterator.hasNext())
                {
                    Map.Entry entry= (Map.Entry) iterator.next();
                    /**
                     * 得到列名
                     */
                    String colomunName= (String) entry.getKey();
                    /**
                     * 然后以列名拿到  列名在游标的位子
                     */
                    Integer colmunIndex=cursor.getColumnIndex(colomunName);

                    Field field= (Field) entry.getValue();

                    Class type=field.getType();
                    if(colmunIndex!=-1) {
                        if(type==String.class) {
                            //反射方式赋值
                            field.set(item,cursor.getString(colmunIndex));
                        }else if(type==Double.class) {
                            field.set(item,cursor.getDouble(colmunIndex));
                        }else  if(type==int.class) {
                            field.set(item,cursor.getInt(colmunIndex));
                        }else if(type==Long.class) {
                            field.set(item,cursor.getLong(colmunIndex));
                        }else  if(type==byte[].class) {
                            field.set(item,cursor.getBlob(colmunIndex));
                            /*
                            不支持的类型
                             */
                        }else {
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    /**
     * 封装修改语句
     *
     */
    class DaoCondition
    {
        /**
         * 查询条件
         * name=? && password =?
         */
        private String whereClause;

        private  String[] whereArgs;
        public DaoCondition(Map<String ,String> whereClause) {
            ArrayList list=new ArrayList();
            StringBuilder stringBuilder=new StringBuilder();

            stringBuilder.append(" 1=1 ");
            Set keys=whereClause.keySet();
            Iterator iterator=keys.iterator();
            while (iterator.hasNext())
            {
                String key= (String) iterator.next();
                String value=whereClause.get(key);

                if (value!=null)
                {
                    /*
                    拼接条件查询语句
                    1=1 and name =? and password=?
                     */
                    stringBuilder.append(" and "+key+" =?");
                    /**
                     * ？----》value
                     */
                    list.add(value);
                }
            }
            this.whereClause = stringBuilder.toString();
            this.whereArgs = (String[]) list.toArray(new String[list.size()]);

        }

        public String[] getWhereArgs() {
            return whereArgs;
        }

        public String getWhereClause() {
            return whereClause;
        }
    }

    public abstract String createTable();
}
