package com.odong.itpkg.service;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午10:59
 */
public interface NetworkService {
    Host getHost(long id);

    Ip getIp(long id);
}
