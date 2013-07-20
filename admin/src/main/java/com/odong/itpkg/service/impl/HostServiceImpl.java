package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.net.HostDao;
import com.odong.itpkg.dao.net.IpDao;
import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.net.dns.Domain;
import com.odong.itpkg.entity.net.dns.Zone;
import com.odong.itpkg.entity.net.firewall.*;
import com.odong.itpkg.service.HostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午10:59
 */
@Service
public class HostServiceImpl implements HostService {
    @Override
    public void setHostKey(long hostId, String key) {
        //
    }

    @Override
    public DateLimit getDateLimit(long dateLimitId) {
        return null;  //
    }

    @Override
    public Mac getMac(long macId) {
        return null;  //
    }

    @Override
    public Output getFirewallOutput(long outputId) {
        return null;  //
    }

    @Override
    public FlowLimit getFlowLimit(long flowId) {
        return null;  //
    }

    @Override
    public List<MacOutput> listFirewallMacOutputByHost(long hostId) {
        return null;  //
    }

    @Override
    public List<MacOutput> listFirewallMacOutputByMac(long macId) {
        return null;  //
    }

    @Override
    public List<MacOutput> listFirewallMacOutputByOutput(long outputId) {
        return null;  //
    }

    @Override
    public List<Input> listFirewallInput(long hostId) {
        return null;  //
    }

    @Override
    public List<Output> listFirewallOutput(long hostId) {
        return null;  //
    }

    @Override
    public List<Nat> listFirewallNat(long hostId) {
        return null;  //
    }

    @Override
    public List<FlowLimit> listFlowLimit(String companyId) {
        return null;  //
    }

    @Override
    public List<Mac> listMac(long hostId) {
        return null;  //
    }

    @Override
    public List<Zone> listDnsZone(long hostId) {
        return null;  //
    }

    @Override
    public List<Domain> listDnsDomain(long zoneId) {
        return null;  //
    }

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
