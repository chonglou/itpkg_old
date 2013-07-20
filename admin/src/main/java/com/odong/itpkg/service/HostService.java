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
    void setHostKey(long hostId, String signKey);

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

    Ip getIp(long ipId);
}
