package com.yyggee.eggs.repositories.dc1;


import java.util.List;

public interface CacheRepository<T> {

    T save(T model) throws Exception;
    List<T> findAll() throws Exception;
    T findByCode(String code) throws Exception;
    T update(T model) throws Exception;
    boolean delete(String code) throws Exception;
}
