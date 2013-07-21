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
    void setIpInfo(String id, String address, String netmask, String gateway, String dns1, String dns2);

    void setStaticIp(String id, String address, String netmask, String gateway, String dns1, String dns2);

    void setDhcpIp(String id);

    void setPppoeIp(String id, String username, String password);

    void addStaticIp(long host, String id, String address, String netmask, String gateway, String dns1, String dns2);

    void addDhcpIp(long host, String id);

    void addPppoeIp(long host, String id, String username, String password);

    void setHostInfo(long hostId, String name, String details);

    void setHostWan(long hostId, int rpcPort, String wanMac);

    void setHostLan(long hostId, String lanNet, String lanMac);

    void setHostDomain(long hostId, String domain);

    List<Host> listHost(String companyId);

    void addHost(String companyId, String name, String domain,
                 String wanIp, String wanMac, int rpcPort,
                 String lanNet, String lanMac,
                 String details);

    String resetHostSignKey(long hostId);

    void setHostState(long hostId, Host.State state);

    Host getHost(String wanMac);

    DateLimit getDateLimit(long dateLimitId);

    Mac getMac(long macId);

    Output getFirewallOutput(long outputId);

    FlowLimit getFlowLimit(long flowId);

    List<MacOutput> listFirewallMacOutputByHost(long hostId);

    List<MacOutput> listFirewallMacOutputByMac(long macId);

    List<MacOutput> listFirewallMacOutputByOutput(long outputId);

    List<Input> listFirewallInput(long hostId);

    List<Output> listFirewallOutput(long hostId);

    List<Nat> listFirewallNat(long hostId);

    List<FlowLimit> listFlowLimit(String companyId);

    List<Mac> listMac(long hostId);

    List<Zone> listDnsZone(long hostId);

    List<Domain> listDnsDomain(long zoneId);

    Host getHost(long hostId);

    Ip getIp(String ipId);
}
