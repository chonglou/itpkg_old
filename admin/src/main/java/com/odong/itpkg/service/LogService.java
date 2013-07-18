package com.odong.itpkg.service;

import com.odong.itpkg.entity.uc.Log;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:57
 */
public interface LogService {
    void add(Long user, String message, Log.Type type);
    void removeOld(int daysKeep);
}
