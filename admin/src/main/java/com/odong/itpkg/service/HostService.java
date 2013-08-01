package com.odong.itpkg.service;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.net.dns.Domain;
import com.odong.itpkg.entity.net.dns.Zone;
import com.odong.itpkg.entity.net.firewall.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午10:59
 */
public interface HostService {


    void addFirewallDmz(long hostId, String name, String wanIp, int lanIp);

    List<Dmz> listFirewallDmz(long hostId);

    void setFirewallDmzInfo(long dmzId, String name);

    void setFirewallDmzRule(long dmzId, String wanIp, int lanIp);

    void delFirewallDmz(long dmzId);

    void addFirewallInput(long hostId, String name, String sIp, int port, Protocol protocol);

    void setFirewallInputInfo(long inputId, String name);

    void setFirewallInputRule(long inputId, String sIp, int port, Protocol protocol);

    void delFirewallInput(long inputId);

    void addFirewallOutput(long hostId, String name, String key, long dateLimitId);

    void setFirewallOutputInfo(long outputId, String name);

    void setFirewallOutputRule(long outputId, String key, long dateLimitId);

    void delFirewallOutput(long outputId);

    void addFirewallNat(long hostId, String name, int sPort, Protocol protocol, int dIp, int dPort);

    void setFirewallNatInfo(long natId, String name);

    void setFirewallNatRule(long natId, int sPort, Protocol protocol, int dIp, int dPort);

    void delFirewallNat(long natId);


    void addFirewallFlowLimit(String companyId,
                              String name, String details,
                              int upRate, int upCeil,
                              int downRate, int downCeil);

    void setFirewallFlowLimitInfo(long flowLimitId, String name, String details);

    void setFirewallFlowLimitLine(long flowLimitId, int upRate, int upCeil, int downRate, int downCeil);

    List<Mac> listMacByFirewallFlowLimit(long flowLimitId);

    void delFirewallFlowLimit(long flowLimitId);

    List<DateLimit> listFirewallDateLimit(String companyId);

    void addFirewallDateLimit(String companyId,
                              String name, String details,
                              int beginHour, int beginMinute,
                              int endHour, int endMinute,
                              boolean mon, boolean tues, boolean wed, boolean thur, boolean fri, boolean sat, boolean sun);

    void setFirewallDateLimitInfo(long dateLimitId, String name, String details);

    void setFirewallDateLimitTime(long dateLimitId, int beginHour, int beginMinute, int endHour, int endMinute);

    void setFirewallDateLimitWeekdays(long dateLimitId, boolean mon, boolean tues, boolean wed, boolean thur, boolean fri, boolean sat, boolean sun);

    List<Output> listFirewallOutputByDateLimit(long dateLimitId);

    void delFirewallDateLimit(long dateLimitId);

    Zone getDnsZone(long zoneId);

    Zone getDnsZone(String name, long hostId);

    void addDnsZone(long hostId, String name, String details);

    void setDnsZone(long zoneId, String details);

    void delDnsZone(long zoneId);

    void addDnsDomainA(long zoneId, String name, String wanIp);

    void addDnsDomainA(long zoneId, String name, Integer lanIp);

    void addDnsDomainNS(long zoneId, String name, String wanIp);

    void addDnsDomainNS(long zoneId, String name, Integer lanIp);

    void addDnsDomainMX(long zoneId, String name, String wanIp, int priority);

    void addDnsDomainMX(long zoneId, String name, Integer lanIp, int priority);

    void delDnsDomain(long domainId);

    void addMac(long hostId, String serial, int ip, Long flowLimit);

    void setMacState(long macId, Mac.State state);

    void setMacInfo(long macId, String hostname, String detail);

    void bindIp2Mac(long macId, int ip, boolean bind);

    void addMac2Output(long macId, long outputId, boolean bind);

    void setMacLimit(long macId, long flowLimitId);

    void setMacUser(long macId, long userId);

    void delMac(long macId);

    void setIpInfo(String id, String address, String netmask, String gateway, String dns1, String dns2);

    void setIpStatic(String id, String address, String netmask, String gateway, String dns1, String dns2);

    void setIpDhcp(String id);

    void setIpPppoe(String id, String username, String password);

    void addIpStatic(String id, String address, String netmask, String gateway, String dns1, String dns2);

    void addIpDhcp(String id);

    void addIpPppoe(String id, String username, String password);

    void setHostInfo(long hostId, String name, String domain, String details);

    void setHostWan(long hostId, int rpcPort, String wanMac);

    void setHostLan(long hostId, String lanNet, String lanMac, long defFlowLimit);

    void setHostDmz(long hostId, boolean enable);

    void setHostDmz(long hostId, String dmzNet, String dmzMac);

    List<Ip> listIpByHost(long hostId);

    List<Host> listHostByCompany(String companyId);

    List<Host> listHost();

    List<Host> listHostByFlowLimit(long flowLimitId);

    void addHost(String companyId, String name, String domain,
                 String wanIp, String wanMac, int rpcPort,
                 String lanNet, String lanMac, long defFlowLimit,
                 String details);

    String resetHostSignKey(long hostId);

    void setHostState(long hostId, Host.State state);

    Host getHost(String wanMac);

    DateLimit getFirewallDateLimit(long dateLimitId);

    Mac getMac(long macId);

    Mac getMac(long hostId, String serial);

    Output getFirewallOutput(long outputId);

    FlowLimit getFirewallFlowLimit(long flowId);

    List<MacOutput> listFirewallMacOutputByMac(long macId);

    List<MacOutput> listFirewallMacOutputByOutput(long outputId);

    List<Input> listFirewallInput(long hostId);

    List<Output> listFirewallOutputByHost(long hostId);

    List<Nat> listFirewallNat(long hostId);

    List<FlowLimit> listFirewallFlowLimit(String companyId);

    List<Mac> listMacByHost(long hostId);

    List<Zone> listDnsZone(long hostId);

    List<Domain> listDnsDomainByZone(long zoneId);

    List<Domain> listDnsDomainByHost(long hostId);


    Host getHost(long hostId);

    Ip getIp(String ipId);
}
