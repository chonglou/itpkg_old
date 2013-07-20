package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.net.HostDao;
import com.odong.itpkg.dao.net.IpDao;
import com.odong.itpkg.dao.net.MacDao;
import com.odong.itpkg.dao.net.dns.DomainDao;
import com.odong.itpkg.dao.net.dns.ZoneDao;
import com.odong.itpkg.dao.net.firewall.*;
import com.odong.itpkg.dao.uc.CompanyDao;
import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.net.dns.Domain;
import com.odong.itpkg.entity.net.dns.Zone;
import com.odong.itpkg.entity.net.firewall.*;
import com.odong.itpkg.service.HostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午10:59
 */
@Service
public class HostServiceImpl implements HostService {
    @Override
    public void setHostKey(long hostId, String signKey) {
        Host host = hostDao.select(hostId);
        host.setSignKey(signKey);
        hostDao.update(host);
    }

    @Override
    public DateLimit getDateLimit(long dateLimitId) {
        return dateLimitDao.select(dateLimitId);  //
    }

    @Override
    public Mac getMac(long macId) {
        return macDao.select(macId);  //
    }

    @Override
    public Output getFirewallOutput(long outputId) {
        return outputDao.select(outputId);  //
    }

    @Override
    public FlowLimit getFlowLimit(long flowId) {
        return flowLimitDao.select(flowId);  //
    }

    @Override
    public List<MacOutput> listFirewallMacOutputByHost(long hostId) {
        Map<String,Object> map = new HashMap<>();
        map.put("host", hostId);
        return macOutputDao.list("SELECT MacOutput AS i WHERE i.host=:host", map);

    }

    @Override
    public List<MacOutput> listFirewallMacOutputByMac(long macId) {
        Map<String,Object> map = new HashMap<>();
        map.put("mac", macId);
        return macOutputDao.list("SELECT MacOutput AS i WHERE i.mac=:mac", map);
    }

    @Override
    public List<MacOutput> listFirewallMacOutputByOutput(long outputId) {

        Map<String,Object> map = new HashMap<>();
        map.put("output", outputId);
        return macOutputDao.list("SELECT MacOutput AS i WHERE i.output=:output", map);
    }

    @Override
    public List<Input> listFirewallInput(long hostId) {
        Map<String,Object> map = new HashMap<>();
        map.put("host", hostId);
        return inputDao.list("SELECT Input AS i WHERE i.host=:host", map);
    }

    @Override
    public List<Output> listFirewallOutput(long hostId) {
        Map<String,Object> map = new HashMap<>();
        map.put("host", hostId);
        return outputDao.list("SELECT Output AS i WHERE i.host=:host", map);
    }

    @Override
    public List<Nat> listFirewallNat(long hostId) {
        Map<String,Object> map = new HashMap<>();
        map.put("host", hostId);
        return natDao.list("SELECT Nat AS i WHERE i.host=:host", map);
    }

    @Override
    public List<FlowLimit> listFlowLimit(String companyId) {
        Map<String,Object> map = new HashMap<>();
        map.put("company", companyId);
        return flowLimitDao.list("SELECT FlowLimit AS i WHERE i.company=:company", map);
    }

    @Override
    public List<Mac> listMac(long hostId) {
        Map<String,Object> map = new HashMap<>();
        map.put("host", hostId);
        return macDao.list("SELECT Mac AS i WHERE i.host=:host", map);

    }

    @Override
    public List<Zone> listDnsZone(long hostId) {
        Map<String,Object> map = new HashMap<>();
        map.put("host", hostId);
        return zoneDao.list("SELECT Zone AS i WHERE i.host=:host", map);  //
    }

    @Override
    public List<Domain> listDnsDomain(long zoneId) {
        Map<String,Object> map = new HashMap<>();
        map.put("zone", zoneId);
        return domainDao.list("SELECT Domain AS i WHERE i.zone=:zone", map);  //
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
    private DomainDao domainDao;
    @Resource
    private ZoneDao zoneDao;
    @Resource
    private DateLimitDao dateLimitDao;
    @Resource
    private FlowLimitDao flowLimitDao;
    @Resource
    private InputDao inputDao;
    @Resource
    private MacOutputDao macOutputDao;
    @Resource
    private NatDao natDao;
    @Resource
    private OutputDao outputDao;
    @Resource
    private MacDao macDao;
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

    public void setDomainDao(DomainDao domainDao) {
        this.domainDao = domainDao;
    }

    public void setZoneDao(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }

    public void setDateLimitDao(DateLimitDao dateLimitDao) {
        this.dateLimitDao = dateLimitDao;
    }

    public void setFlowLimitDao(FlowLimitDao flowLimitDao) {
        this.flowLimitDao = flowLimitDao;
    }

    public void setInputDao(InputDao inputDao) {
        this.inputDao = inputDao;
    }

    public void setMacOutputDao(MacOutputDao macOutputDao) {
        this.macOutputDao = macOutputDao;
    }

    public void setNatDao(NatDao natDao) {
        this.natDao = natDao;
    }

    public void setOutputDao(OutputDao outputDao) {
        this.outputDao = outputDao;
    }

    public void setMacDao(MacDao macDao) {
        this.macDao = macDao;
    }
}
