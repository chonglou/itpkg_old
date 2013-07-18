package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.HostDao;
import com.odong.itpkg.dao.IpDao;
import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.service.NetworkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午10:59
 */
@Service
public class NetworkServiceImpl implements NetworkService {
    @Override
    public Host getHost(long id) {
        return hostDao.select(id);  //
    }

    @Override
    public Ip getIp(long id) {
        return ipDao.select(id);  //
    }

    @Resource
    private IpDao ipDao;
    @Resource
    private HostDao hostDao;

    public void setIpDao(IpDao ipDao) {
        this.ipDao = ipDao;
    }

    public void setHostDao(HostDao hostDao) {
        this.hostDao = hostDao;
    }
}
