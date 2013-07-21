package com.odong.itpkg.service.impl;

import com.odong.itpkg.dao.net.HostDao;
import com.odong.itpkg.dao.net.IpDao;
import com.odong.itpkg.dao.net.MacDao;
import com.odong.itpkg.dao.net.dns.DomainDao;
import com.odong.itpkg.dao.net.dns.ZoneDao;
import com.odong.itpkg.dao.net.firewall.*;
import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.net.dns.Domain;
import com.odong.itpkg.entity.net.dns.Zone;
import com.odong.itpkg.entity.net.firewall.*;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.StringHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
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
    public void setIpInfo(String id, String address, String netmask, String gateway, String dns1, String dns2) {
        Ip ip = ipDao.select(id);
        ip.setAddress(address);
        ip.setNetmask(netmask);
        ip.setGateway(gateway);
        ip.setDns1(dns1);
        ip.setDns2(dns2);
        ipDao.update(ip);
    }

    @Override
    public void setStaticIp(String id, String address, String netmask, String gateway, String dns1, String dns2) {
        Ip ip = ipDao.select(id);
        ip.setAddress(address);
        ip.setNetmask(netmask);
        ip.setGateway(gateway);
        ip.setDns1(dns1);
        ip.setDns2(dns2);
        ip.setType(Ip.Type.STATIC);
        ipDao.update(ip);
    }

    @Override
    public void setDhcpIp(String id) {
        Ip ip = ipDao.select(id);
        ip.setType(Ip.Type.DHCP);
        ipDao.update(ip);
    }

    @Override
    public void setPppoeIp(String id, String username, String password) {
        Ip ip = ipDao.select(id);
        ip.setUsername(username);
        ip.setPassword(password);
        ip.setType(Ip.Type.PPPOE);
        ipDao.update(ip);
    }

    @Override
    public void addStaticIp(long host, String id, String address, String netmask, String gateway, String dns1, String dns2) {
        Ip ip = new Ip();
        ip.setId(id);
        ip.setHost(host);
        ip.setAddress(address);
        ip.setNetmask(netmask);
        ip.setGateway(gateway);
        ip.setDns1(dns1);
        ip.setDns2(dns2);
        ip.setType(Ip.Type.STATIC);
        ip.setCreated(new Date());
        ipDao.insert(ip);
    }

    @Override
    public void addDhcpIp(long host, String id) {
        Ip ip = new Ip();
        ip.setId(id);
        ip.setHost(host);
        ip.setType(Ip.Type.DHCP);
        ip.setCreated(new Date());
        ipDao.insert(ip);
    }

    @Override
    public void addPppoeIp(long host, String id, String username, String password) {
        Ip ip = new Ip();
        ip.setId(id);
        ip.setHost(host);
        ip.setUsername(username);
        ip.setPassword(password);
        ip.setType(Ip.Type.PPPOE);
        ip.setCreated(new Date());
        ipDao.insert(ip);
    }

    @Override
    public void setHostInfo(long hostId, String name, String details) {
        Host h = hostDao.select(hostId);
        h.setName(name);
        h.setDetails(details);
        hostDao.update(h);
    }

    @Override
    public void setHostWan(long hostId, int rpcPort, String wanMac) {
        Host h = hostDao.select(hostId);
        h.setRpcPort(rpcPort);
        h.setWanMac(wanMac);
        hostDao.update(h);
    }

    @Override
    public void setHostLan(long hostId, String lanNet, String lanMac) {
        Host h = hostDao.select(hostId);
        h.setLanNet(lanNet);
        h.setLanMac(lanMac);
        hostDao.update(h);
    }

    @Override
    public void setHostDomain(long hostId, String domain) {
        Host h = hostDao.select(hostId);
        h.setDomain(domain);
        hostDao.update(h);
    }

    @Override
    public List<Host> listHost(String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("company", companyId);
        return hostDao.list("SELECT Host AS i WHERE i.company=:company", map);
    }

    @Override
    public void addHost(String companyId, String name, String domain,
                        String wanIp, String wanMac, int rpcPort,
                        String lanNet, String lanMac,
                        String details) {
        Host h = new Host();
        h.setCompany(companyId);
        h.setName(name);
        h.setDomain(domain);
        h.setWanIp(wanIp);
        h.setWanMac(wanMac);
        h.setRpcPort(rpcPort);
        h.setLanNet(lanNet);
        h.setLanMac(lanMac);
        h.setDetails(details);
        h.setState(Host.State.SUBMIT);
        h.setSignKey(encryptHelper.encode(stringHelper.random(Host.KEY_LEN)));
        h.setCreated(new Date());
        hostDao.insert(h);
    }

    @Override
    public String resetHostSignKey(long hostId) {
        String key = stringHelper.random(Host.KEY_LEN);
        Host host = hostDao.select(hostId);
        host.setSignKey(encryptHelper.encode(key));
        hostDao.update(host);
        return key;
    }

    @Override
    public void setHostState(long hostId, Host.State state) {
        Host host = hostDao.select(hostId);
        host.setState(state);
        hostDao.update(host);
    }

    @Override
    public Host getHost(String wanMac) {
        Map<String,Object> map = new HashMap<>();
        map.put("wanMac", wanMac);
        return hostDao.select("SELECT Host AS i WHERE i.wanMac=:wanMac", map);
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
    public Ip getIp(String id) {
        return ipDao.select(id);  //
    }

    @Resource
    private StringHelper stringHelper;
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
    @Resource
    private EncryptHelper encryptHelper;

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setStringHelper(StringHelper stringHelper) {
        this.stringHelper = stringHelper;
    }

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
