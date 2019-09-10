package com.derek.db;

import java.util.List;

public interface IBaseDao<T> {
    Long insert(T entity);
    int update(T entity,T where);
    int delete(T where);

    List<T> query(T where);
    List<T> query(T where,String orderBy,Integer startIndex,Integer limit);
    List<T> query(String sql);
}
