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
    public void addFirewallDmz(long hostId, String name, String wanIp, int lanIp) {
        Dmz d = new Dmz();
        d.setHost(hostId);
        d.setCreated(new Date());
        d.setName(name);
        d.setWanIp(wanIp);
        d.setLanIp(lanIp);
        dmzDao.insert(d);
    }

    @Override
    public List<Dmz> listFirewallDmz(long hostId) {
        Map<String, Object> map = new HashMap<>();
        map.put("host", hostId);
        return dmzDao.list("FROM Dmz AS i WHERE i.host=:hostId", map);  //
    }

    @Override
    public void setFirewallDmzInfo(long dmzId, String name) {
        Dmz d = dmzDao.select(dmzId);
        d.setName(name);
        dmzDao.update(d);
    }

    @Override
    public void setFirewallDmzRule(long dmzId, String wanIp, int lanIp) {
        Dmz d = dmzDao.select(dmzId);
        d.setWanIp(wanIp);
        d.setLanIp(lanIp);
        dmzDao.update(d);
    }

    @Override
    public void delFirewallDmz(long dmzId) {
        dmzDao.delete(dmzId);
    }

    @Override
    public void addFirewallInput(long hostId, String name, String sIp, int port, Protocol protocol) {
        Input i = new Input();
        i.setHost(hostId);
        i.setCreated(new Date());
        i.setName(name);
        i.setsIp(sIp);
        i.setPort(port);
        i.setProtocol(protocol);
        inputDao.insert(i);
    }

    @Override
    public void setFirewallInputInfo(long inputId, String name) {
        Input i = inputDao.select(inputId);
        i.setName(name);
        inputDao.update(i);
    }

    @Override
    public void setFirewallInputRule(long inputId, String sIp, int port, Protocol protocol) {
        Input i = inputDao.select(inputId);
        i.setsIp(sIp);
        i.setPort(port);
        i.setProtocol(protocol);
        inputDao.update(i);
    }

    @Override
    public void delFirewallInput(long inputId) {
        inputDao.delete(inputId);
    }

    @Override
    public void addFirewallOutput(long hostId, String name, String key, long dateLimitId) {
        Output o = new Output();
        o.setName(name);
        o.setHost(hostId);
        o.setKey(key);
        o.setDateLimit(dateLimitId);
        o.setCreated(new Date());
        outputDao.insert(o);
    }

    @Override
    public void setFirewallOutputInfo(long outputId, String name) {
        Output o = outputDao.select(outputId);
        o.setName(name);
        outputDao.update(o);
    }

    @Override
    public void setFirewallOutputRule(long outputId, String key, long dateLimitId) {
        Output o = outputDao.select(outputId);
        o.setKey(key);
        o.setDateLimit(dateLimitId);
        outputDao.update(o);
    }

    @Override
    public void delFirewallOutput(long outputId) {
        Map<String, Object> map = new HashMap<>();
        map.put("output", outputId);
        macOutputDao.delete("DELETE MacOutput AS i WHERE i.output=:output", map);
        outputDao.delete(outputId);
    }

    @Override
    public void addFirewallNat(long hostId, String name, int sPort, Protocol protocol, int dIp, int dPort) {
        Nat n = new Nat();
        n.setCreated(new Date());
        n.setHost(hostId);
        n.setName(name);
        n.setsPort(sPort);
        n.setProtocol(protocol);
        n.setdIp(dIp);
        n.setdPort(dPort);
        n.setProtocol(protocol);
        natDao.insert(n);
    }

    @Override
    public void setFirewallNatInfo(long natId, String name) {
        Nat n = natDao.select(natId);
        n.setName(name);
        natDao.update(n);
    }

    @Override
    public void setFirewallNatRule(long natId, int sPort, Protocol protocol, int dIp, int dPort) {
        Nat n = natDao.select(natId);
        n.setsPort(sPort);
        n.setProtocol(protocol);
        n.setdIp(dIp);
        n.setdPort(dPort);
        natDao.update(n);
    }

    @Override
    public void delFirewallNat(long natId) {
        natDao.delete(natId);
    }


    @Override
    public void addFirewallFlowLimit(String companyId, String name, String details, int upRate, int upCeil, int downRate, int downCeil) {
        FlowLimit fl = new FlowLimit();
        fl.setCompany(companyId);
        fl.setName(name);
        fl.setDetails(details);
        fl.setUpCeil(upCeil);
        fl.setUpRate(upRate);
        fl.setDownCeil(downCeil);
        fl.setDownRate(downRate);
        fl.setCreated(new Date());
        flowLimitDao.insert(fl);
    }

    @Override
    public void setFirewallFlowLimitInfo(long flowLimitId, String name, String details) {
        FlowLimit fl = flowLimitDao.select(flowLimitId);
        fl.setName(name);
        fl.setDetails(details);
        flowLimitDao.update(fl);
    }

    @Override
    public void setFirewallFlowLimitTime(long flowLimitId, int upRate, int upCeil, int downRate, int downCeil) {
        FlowLimit fl = flowLimitDao.select(flowLimitId);
        fl.setUpCeil(upCeil);
        fl.setUpRate(upRate);
        fl.setDownCeil(downCeil);
        fl.setDownRate(downRate);
        flowLimitDao.update(fl);
    }

    @Override
    public List<Mac> listMacByFirewallFlowLimit(long flowLimitId) {
        Map<String, Object> map = new HashMap<>();
        map.put("flowLimit", flowLimitId);
        return macDao.list("FROM Mac AS i WHERE i.flowLimit=:flowLimit", map);  //
    }

    @Override
    public void delFirewallFlowLimit(long flowLimitId) {
        flowLimitDao.delete(flowLimitId);
    }

    @Override
    public List<DateLimit> listFirewallDateLimit(String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("company", companyId);
        return dateLimitDao.list("FROM DateLimit AS i WHERE i.company=:company", map);  //

    }

    @Override
    public void addFirewallDateLimit(String companyId, String name, String details, int beginHour, int beginMinute, int endHour, int endMinute, boolean mon, boolean tues, boolean wed, boolean thur, boolean fri, boolean sat, boolean sun) {
        DateLimit dl = new DateLimit();
        dl.setCompany(companyId);
        dl.setName(name);
        dl.setDetails(details);
        dl.setBeginHour(beginHour);
        dl.setBeginMinute(beginMinute);
        dl.setEndHour(endHour);
        dl.setEndMinute(endMinute);
        dl.setMon(mon);
        dl.setTues(tues);
        dl.setWed(wed);
        dl.setThur(thur);
        dl.setFri(fri);
        dl.setSat(sat);
        dl.setSun(sun);
        dl.setCreated(new Date());
        dateLimitDao.insert(dl);
    }

    @Override
    public void setFirewallDateLimitInfo(long dateLimitId, String name, String details) {
        DateLimit dl = dateLimitDao.select(dateLimitId);
        dl.setName(name);
        dl.setDetails(details);
        dateLimitDao.update(dl);
    }

    @Override
    public void setFirewallDateLimitTime(long dateLimitId, int beginHour, int beginMinute, int endHour, int endMinute) {
        DateLimit dl = dateLimitDao.select(dateLimitId);
        dl.setBeginHour(beginHour);
        dl.setBeginMinute(beginMinute);
        dl.setEndHour(endHour);
        dl.setEndMinute(endMinute);
        dateLimitDao.update(dl);
    }

    @Override
    public void setFirewallDateLimitWeekdays(long dateLimitId, boolean mon, boolean tues, boolean wed, boolean thur, boolean fri, boolean sat, boolean sun) {
        DateLimit dl = dateLimitDao.select(dateLimitId);
        dl.setMon(mon);
        dl.setTues(tues);
        dl.setWed(wed);
        dl.setThur(thur);
        dl.setFri(fri);
        dl.setSat(sat);
        dl.setSun(sun);
        dateLimitDao.update(dl);
    }

    @Override
    public List<Output> listFirewallOutputByDateLimit(long dateLimitId) {
        Map<String, Object> map = new HashMap<>();
        map.put("dateLimit", dateLimitId);
        return outputDao.list("FROM Output AS i WHERE i.dateLimit=:dateLimit", map);  //
    }

    @Override
    public void delFirewallDateLimit(long dateLimitId) {
        dateLimitDao.delete(dateLimitId);
    }

    @Override
    public Zone getZone(long zoneId) {
        return zoneDao.select(zoneId);
    }

    @Override
    public Zone getZone(String name, long hostId) {
        Map<String,Object> map = new HashMap<>();
        map.put("name", name);
        map.put("host", hostId);
        return zoneDao.select("SELECT Zone AS i WHERE i.name=:nam AND i.host=:host", map);
    }

    @Override
    public void addDnsZone(long hostId, String name, String details) {
        Zone z = new Zone();
        z.setHost(hostId);
        z.setName(name);
        z.setDetails(details);
        z.setCreated(new Date());
        zoneDao.insert(z);
    }

    @Override
    public void setDnsZone(long zoneId, String details) {
        Zone z = zoneDao.select(zoneId);
        z.setDetails(details);
        zoneDao.update(z);
    }

    @Override
    public void delDnsZone(long zoneId) {
        Map<String, Object> map = new HashMap<>();
        map.put("zone", zoneId);
        domainDao.delete("DELETE Domain AS i WHERE i.zone=:zone", map);
        zoneDao.delete(zoneId);
    }

    @Override
    public void addDnsDomainA(long zoneId, String name, String wanIp) {
        Domain d = new Domain();
        d.setName(name);
        d.setZone(zoneId);
        d.setWanIp(wanIp);
        d.setType(Domain.Type.A);
        d.setCreated(new Date());
        domainDao.insert(d);
    }

    @Override
    public void addDnsDomainA(long zoneId, String name, Integer lanIp) {
        Domain d = new Domain();
        d.setName(name);
        d.setLanIp(lanIp);
        d.setZone(zoneId);
        d.setLocal(true);
        d.setType(Domain.Type.A);
        d.setCreated(new Date());
        domainDao.insert(d);
    }

    @Override
    public void addDnsDomainNS(long zoneId, String name, String wanIp) {
        Domain d = new Domain();
        d.setName(name);
        d.setZone(zoneId);
        d.setWanIp(wanIp);
        d.setType(Domain.Type.NS);
        d.setCreated(new Date());
        domainDao.insert(d);
    }

    @Override
    public void addDnsDomainNS(long zoneId, String name, Integer lanIp) {
        Domain d = new Domain();
        d.setName(name);
        d.setZone(zoneId);
        d.setLanIp(lanIp);
        d.setLocal(true);
        d.setType(Domain.Type.NS);
        d.setCreated(new Date());
        domainDao.insert(d);
    }

    @Override
    public void addDnsDomainMX(long zoneId, String name, String wanIp, int priority) {
        Domain d = new Domain();
        d.setName(name);
        d.setZone(zoneId);
        d.setWanIp(wanIp);
        d.setPriority(priority);
        d.setType(Domain.Type.MX);
        d.setCreated(new Date());
        domainDao.insert(d);
    }

    @Override
    public void addDnsDomainMX(long zoneId, String name, Integer lanIp, int priority) {
        Domain d = new Domain();
        d.setName(name);
        d.setLanIp(lanIp);
        d.setZone(zoneId);
        d.setPriority(priority);
        d.setType(Domain.Type.MX);
        d.setCreated(new Date());
        domainDao.insert(d);
    }

    @Override
    public void delDnsDomain(long domainId) {
        domainDao.delete(domainId);
    }

    @Override
    public void addMac(long hostId, String serial, int ip, Long flowLimit) {

        Mac m = new Mac();
        m.setIp(ip);
        m.setSerial(serial);
        m.setCreated(new Date());
        m.setState(Mac.State.SUBMIT);
        m.setFlowLimit(flowLimit);
        macDao.insert(m);

    }

    @Override
    public void setMacState(long macId, Mac.State state) {
        Mac m = macDao.select(macId);
        m.setState(state);
        macDao.update(m);
    }

    @Override
    public void setMacInfo(long macId, String hostname, String detail) {
        Mac m = macDao.select(macId);
        m.setHostname(hostname);
        m.setDetail(detail);
        macDao.update(m);
    }

    @Override
    public void bindIp2Mac(long macId, int ip, boolean bind) {
        Mac m = macDao.select(macId);
        m.setIp(ip);
        m.setBind(bind);
        macDao.update(m);
    }

    @Override
    public void addMac2Output(long macId, long outputId, boolean bind) {
        Map<String, Object> map = new HashMap<>();
        map.put("mac", macId);
        map.put("output", outputId);
        MacOutput mo = macOutputDao.select("FROM MacOutput AS i WHERE i.mac=:mac AND i.output=:output", map);
        if (mo == null) {
            if (bind) {
                mo = new MacOutput();
                mo.setCreated(new Date());
                mo.setMac(macId);
                mo.setOutput(outputId);
                macOutputDao.insert(mo);
            } else {
                throw new IllegalArgumentException("规则[" + macId + "," + outputId + "]不存在");
            }
        } else {
            if (bind) {
                throw new IllegalArgumentException("规则[" + macId + "," + outputId + "]已存在");
            } else {
                macOutputDao.delete(mo.getId());
            }
        }
    }

    @Override
    public void setMacLimit(long macId, long flowLimitId) {
        Mac m = macDao.select(macId);
        m.setFlowLimit(flowLimitId);
        macDao.update(m);
    }

    @Override
    public void setMacUser(long macId, long userId) {
        Mac m = macDao.select(macId);
        m.setUser(userId);
        macDao.update(m);
    }

    @Override
    public void delMac(long macId) {
        Map<String, Object> map = new HashMap<>();
        map.put("mac", macId);
        macOutputDao.delete("DELETE MacOutput AS i WHERE i.mac=:mac", map);
        macDao.delete(macId);
    }


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
    public void setIpStatic(String id, String address, String netmask, String gateway, String dns1, String dns2) {
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
    public void setIpDhcp(String id) {
        Ip ip = ipDao.select(id);
        ip.setType(Ip.Type.DHCP);
        ipDao.update(ip);
    }

    @Override
    public void setIpPppoe(String id, String username, String password) {
        Ip ip = ipDao.select(id);
        ip.setUsername(username);
        ip.setPassword(password);
        ip.setType(Ip.Type.PPPOE);
        ipDao.update(ip);
    }

    @Override
    public void addIpStatic(long host, String id, String address, String netmask, String gateway, String dns1, String dns2) {
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
    public void addIpDhcp(long host, String id) {
        Ip ip = new Ip();
        ip.setId(id);
        ip.setHost(host);
        ip.setType(Ip.Type.DHCP);
        ip.setCreated(new Date());
        ipDao.insert(ip);
    }

    @Override
    public void addIpPppoe(long host, String id, String username, String password) {
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
    public void setHostDmz(long hostId, boolean enable) {
        Host h = hostDao.select(hostId);
        h.setDmz(enable);
        hostDao.update(h);
    }

    @Override
    public void setHostDmz(long hostId, String dmzNet, String dmzMac) {
        Host h = hostDao.select(hostId);
        h.setDmzNet(dmzNet);
        h.setDmzMac(dmzMac);
        hostDao.update(h);
    }

    @Override
    public void setHostDomain(long hostId, String domain) {
        Host h = hostDao.select(hostId);
        h.setDomain(domain);
        hostDao.update(h);
    }

    @Override
    public List<Ip> listIpByHost(long hostId) {
        Map<String, Object> map = new HashMap<>();
        map.put("host", hostId);
        return ipDao.list("SELECT Ip AS i WHERE i.host=:host", map);
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
        Map<String, Object> map = new HashMap<>();
        map.put("wanMac", wanMac);
        return hostDao.select("SELECT Host AS i WHERE i.wanMac=:wanMac", map);
    }

    @Override
    public DateLimit getFirewallDateLimit(long dateLimitId) {
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
    public FlowLimit getFirewallFlowLimit(long flowId) {
        return flowLimitDao.select(flowId);  //
    }


    @Override
    public List<MacOutput> listFirewallMacOutputByMac(long macId) {
        Map<String, Object> map = new HashMap<>();
        map.put("mac", macId);
        return macOutputDao.list("SELECT MacOutput AS i WHERE i.mac=:mac", map);
    }

    @Override
    public List<MacOutput> listFirewallMacOutputByOutput(long outputId) {

        Map<String, Object> map = new HashMap<>();
        map.put("output", outputId);
        return macOutputDao.list("SELECT MacOutput AS i WHERE i.output=:output", map);
    }

    @Override
    public List<Input> listFirewallInput(long hostId) {
        Map<String, Object> map = new HashMap<>();
        map.put("host", hostId);
        return inputDao.list("SELECT Input AS i WHERE i.host=:host", map);
    }

    @Override
    public List<Output> listFirewallOutputByHost(long hostId) {
        Map<String, Object> map = new HashMap<>();
        map.put("host", hostId);
        return outputDao.list("SELECT Output AS i WHERE i.host=:host", map);
    }

    @Override
    public List<Nat> listFirewallNat(long hostId) {
        Map<String, Object> map = new HashMap<>();
        map.put("host", hostId);
        return natDao.list("SELECT Nat AS i WHERE i.host=:host", map);
    }

    @Override
    public List<FlowLimit> listFirewallFlowLimit(String companyId) {
        Map<String, Object> map = new HashMap<>();
        map.put("company", companyId);
        return flowLimitDao.list("SELECT FlowLimit AS i WHERE i.company=:company", map);
    }

    @Override
    public List<Mac> listMacByHost(long hostId) {
        Map<String, Object> map = new HashMap<>();
        map.put("host", hostId);
        return macDao.list("SELECT Mac AS i WHERE i.host=:host", map);

    }

    @Override
    public List<Zone> listDnsZone(long hostId) {
        Map<String, Object> map = new HashMap<>();
        map.put("host", hostId);
        return zoneDao.list("SELECT Zone AS i WHERE i.host=:host", map);  //
    }

    @Override
    public List<Domain> listDnsDomainByZone(long zoneId) {
        Map<String, Object> map = new HashMap<>();
        map.put("zone", zoneId);
        return domainDao.list("SELECT Domain AS i WHERE i.zone=:zone", map);  //
    }

    @Override
    public List<Domain> listDnsDomainByHost(long hostId) {

        Map<String, Object> map = new HashMap<>();
        map.put("host", hostId);
        return domainDao.list("SELECT Domain AS d WHERE d.zone IN (SELECT Zone AS z WHERE z.host=:host)", map);
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
    private DmzDao dmzDao;
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

    public void setDmzDao(DmzDao dmzDao) {
        this.dmzDao = dmzDao;
    }


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
