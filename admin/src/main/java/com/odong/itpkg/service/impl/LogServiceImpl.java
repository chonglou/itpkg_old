package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.uc.LogDao;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.service.LogService;
import com.odong.portal.util.TimeHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:58
 */
@Service
public class LogServiceImpl implements LogService {
    @Override
    public void add(Long user, String message, Log.Type type) {
        Log log = new Log();
        log.setType(type);
        log.setMessage(message);
        log.setUser(user);
        logDao.insert(log);
    }

    @Override
    public void removeOld(int daysKeep) {
        Map<String, Object> map = new HashMap<>();
        map.put("date", timeHelper.plus(new Date(), -60 * 60 * 24 * daysKeep));
        logDao.delete("DELETE Log AS i WHERE i.created < :date", map);
    }

    @Resource
    private LogDao logDao;
    @Resource
    private TimeHelper timeHelper;

    public void setTimeHelper(TimeHelper timeHelper) {
        this.timeHelper = timeHelper;
    }

    public void setLogDao(LogDao logDao) {
        this.logDao = logDao;
    }
}
