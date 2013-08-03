package com.odong.itpkg.linux;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.net.dns.Domain;
import com.odong.itpkg.entity.net.dns.Zone;
import com.odong.itpkg.entity.net.firewall.*;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.service.SiteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午8:29
 */
@Component
public class ArchHelper {
    public List<String> lanNetIdList() {
        List<String> list = jsonHelper.json2List(lanNetExtends, String.class);
        for (int i = 0; i < 255; i++) {
            list.add(i, "192.168." + i + ".0");
        }
        /*
        for(int i=16; i<32; i++){
            for(int j=0; j<255;j++){
                list.add("172."+i+"."+j+".0");
            }
        }
        for(int i=0; i<255;i++){
            for(int j=0; j<255; j++){
                list.add("10."+i+"."+j+".0");
            }
        }
        */
        return list;
    }

    public EtcFile clearProfile(long hostId) {
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/sh\n");
        for (String s : ffClear(hostId)) {
            sb.append(s);
            sb.append("\n");
        }
        for (String s : tcClear()) {
            sb.append(s);
            sb.append("\n");
        }
        return new EtcFile("/opt/itpkgd/clear.sh", "root:root", "500", sb.toString());

    }

    public EtcFile ffProfile(long hostId) {
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/sh\n");
        for (String s : ffApply(hostId)) {
            sb.append(s);
            sb.append("\n");
        }
        return new EtcFile("/opt/itpkgd/ff.sh", "root:root", "500", sb.toString());
    }

    public EtcFile tcProfile(long hostId) {
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/sh\n");
        for (String s : tcApply(hostId)) {
            sb.append(s);
            sb.append("\n");
        }
        return new EtcFile("/opt/itpkgd/tc.sh", "root:root", "500", sb.toString());
    }

    public String saveFf() {
        return "iptables-save > /etc/iptables/iptables.rules";
    }


    public List<String> getWanInfo() {
        List<String> lines = new ArrayList<>();
        lines.add("ifconfig wlan | grep inet |  awk -F\" \" '{print $2\"\\n\"$4}'");
        lines.add("route | grep default | awk -F\" \" '{print $2}'");
        lines.add("cat /etc/resolv.conf | grep nameserver| awk -F\" \" '{print $2}'");
        return lines;
    }

    public List<EtcFile> hostname(long hostId) {
        Host host = hostService.getHost(hostId);
        List<EtcFile> etcs = new ArrayList<>();
        etcs.add(new EtcFile("/etc/hostname", "root:root", "444", host.getDomain()));
        etcs.add(new EtcFile("/etc/hosts", "root:root", "444", String.format("127.0.0.1\tlocalhost.localdomain\tlocalhost\t%s\n", host.getDomain())));
        return etcs;
    }

    public EtcFile daemonProfile(long hostId) {
        Host host = hostService.getHost(hostId);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("server.key=%s\n", encryptHelper.decode(host.getSignKey())));
        sb.append(String.format("server.port=%d\n", host.getRpcPort()));
        sb.append(String.format("server.space=%d\n", host.getSpace()));
        sb.append(String.format("server.http=http://%s\n", siteService.getString("site.domain")));
        return new EtcFile("/opt/itpkgd/config.properties", "root:root", "400", sb.toString());
    }

    public String reboot() {
        return "reboot";
    }


    public String scanMac() {
        return "ifconfig -a| grep ether | awk -F\" \" '{print $2}'";
    }

    public String restartDncp4() {
        return "systemctl restart dhcpd4";
    }

    public String startDncp4() {
        return "systemctl start dhcpd4";
    }

    public String stopDncp4() {
        return "systemctl stop dhcpd4";
    }

    public String statusDncp4() {
        return "systemctl status dhcpd4";
    }

    public String statusBind9() {
        return "systemctl status named";
    }

    public String startBind9() {
        return "systemctl start named";
    }

    public String stopBind9() {
        return "systemctl stop named";
    }

    public String restartBind() {
        return "systemctl restart named";
    }

    public List<String> enableService(long hostId) {
        Host host = hostService.getHost(hostId);
        List<String> lines = new ArrayList<>();
        lines.add("udevadm trigger");
        lines.add("netctl enable wan");
        lines.add("netctl enable lan");
        if (host.isDmz()) {
            lines.add("netctl enable dmz");
        }
        for (String s : new String[]{"dhcpd4", "named", "iptables"}) {
            lines.add(String.format("systemctl enable %s", s));
        }
        return lines;
    }

    public List<EtcFile> networkProfile(long hostId) {
        Host host = hostService.getHost(hostId);
        Ip wanIp = hostService.getIp(host.getWanIp());
        List<EtcFile> etcs = new ArrayList<>();

        StringBuilder sbU = new StringBuilder();
        sbU.append(String.format("SUBSYSTEM==\"net\", ACTION==\"add\", ATTR{address}==\"%s\", NAME=\"wan\"\n", host.getWanMac()));
        sbU.append(String.format("SUBSYSTEM==\"net\", ACTION==\"add\", ATTR{address}==\"%s\", NAME=\"lan\"\n", host.getLanMac()));
        etcs.add(new EtcFile("/etc/udev/rules.d/10-network.rules", "root:root", "444", sbU.toString()));

        StringBuilder sbW = new StringBuilder();
        switch (wanIp.getType()) {
            case STATIC:
                sbW.append("Description='A static ethernet connection for wan'\n");
                sbW.append("Interface=wan\n");
                sbW.append("Connection=ethernet\n");
                sbW.append("IP=static\n");
                sbU.append(String.format("Address=('%s/24'", wanIp.getAddress()));
                if (host.isDmz()) {
                    for (Dmz d : hostService.listFirewallDmz(hostId)) {
                        Ip dmzIp = hostService.getIp(d.getWanIp());
                        sbU.append(String.format(" %s/24", dmzIp.getAddress()));
                    }
                }
                sbU.append(")\n");
                sbW.append(String.format("Gateway='%s'\n", wanIp.getGateway()));
                sbW.append(String.format("DNS=('%s %s')\n", wanIp.getDns1(), wanIp.getDns2()));
                break;
            case DHCP:
                sbW.append("Description='A dhcp ethernet connection for wan'\n");
                sbW.append("Interface=wan\n");
                sbW.append("Connection=ethernet\n");
                sbW.append("IP=dhcp\n");
                break;
            case PPPOE:
                sbW.append("Description='A PPPoE connection for wan'\n");
                sbW.append("Interface=wan\n");
                sbW.append("Connection=pppoe\n");
                sbW.append(String.format("User='%s'\n", wanIp.getUsername()));
                sbW.append(String.format("Password='%s'\n", encryptHelper.decode(wanIp.getPassword())));
                sbW.append("ConnectionMode='persist'\n");
                break;
        }
        etcs.add(new EtcFile("/etc/netctl/wan", "root:root", "444", sbW.toString()));

        StringBuilder sbL = new StringBuilder();
        sbL.append("Description='A static ethernet connection for lan'\n");
        sbL.append("Interface=lan\n");
        sbL.append("Connection=ethernet\n");
        sbL.append("IP=static\n");
        sbL.append(String.format("Address=('%s.1/24')\n", host.getLanNet()));
        etcs.add(new EtcFile("/etc/netctl/lan", "root:root", "444", sbL.toString()));

        return etcs;
    }

    public String hostnameScan(long hostId) {
        Host host = hostService.getHost(hostId);
        return String.format("nbtscan -r %s.0/24", host.getLanNet());
    }

    public String scanHost(String ip) {
        return String.format("name -v %s", ip);
    }

    public String macScan() {
        return "arp -n -i lan";
    }

    public List<String> ffApply(long hostId) {

        Host host = hostService.getHost(hostId);
        Ip wanIp = hostService.getIp(host.getWanIp());

        List<String> lines = ffPrepare();

        lines.add("iptables -F");
        lines.add("iptables -X");
        lines.add("iptables -P INPUT DROP");
        lines.add("iptables -P OUTPUT ACCEPT");
        lines.add("iptables -P FORWARD DROP");
        lines.add("iptables -A INPUT -i lo -j ACCEPT");
        lines.add("iptables -A INPUT -m state --state RELATED -j ACCEPT");
        lines.add("iptables -A FORWARD  -m state --state RELATED -j ACCEPT");

        //ICMP
        for (String p : new String[]{"0", "3", "3/4", "4", "11", "12", "14", "16", "18"}) {
            lines.add(String.format("iptables -A INPUT -i wan -p icmp --icmp-type %s -j ACCEPT", p));
        }
        if (host.isPing()) {
            lines.add("iptables -A INPUT -i wan -p icmp --icmp-type 8 -j ACCEPT");
        }
        //防火墙放开的TCP端口
        for (int i : new int[]{22, host.getRpcPort()}) {
            lines.add(String.format("iptables -A INPUT -p TCP -d %s --dport %d -j ACCEPT", wanIp.getAddress(), i));
        }
        //INPUT
        for (Input in : hostService.listFirewallInput(hostId)) {
            lines.add(in.getsIp() == null ?
                    String.format("iptables -A INPUT -p %s -i wan --dport %d -j ACCEPT", in.getProtocol(), in.getPort())
                    :
                    String.format("iptables -A INPUT -s %s -p %s -i wan --dport %d -j ACCEPT", in.getsIp(), in.getProtocol(), in.getPort())
            );
        }
        //LOOP
        lines.add("iptables -A INPUT -i lan -j ACCEPT");
        lines.add("iptables -A INPUT -m state --state ESTABLISHED -j ACCEPT");

        //OUTPUT
        for (Output out : hostService.listFirewallOutputByHost(hostId)) {
            DateLimit dl = hostService.getFirewallDateLimit(out.getDateLimit());
            for (MacOutput mo : hostService.listFirewallMacOutputByOutput(out.getId())) {
                Mac m = hostService.getMac(mo.getMac());
                if (m.getState() == Mac.State.ENABLE) {
                    lines.add(String.format("iptables -A FORWARD  -m mac --mac-source %s -i lan -m time --kerneltz --timestart %02d:%02d --timestop %02d:%02d --weekdays %s -m string --string \"%s\" --algo bm -j ACCEPT",
                            m.getSerial(), dl.getBeginHour(), dl.getBeginMinute(), dl.getEndHour(), dl.getEndMinute(), dl.toWeeks(), out.getKey()));
                }
            }
            lines.add(String.format("'iptables -A FORWARD -i lan -m time --kerneltz --timestart %02d:%02d --timestop %02d:%02d --weekdays %s -m string --string \"%s\" --algo bm -j DROP",
                    dl.getBeginHour(), dl.getBeginMinute(), dl.getEndHour(), dl.getEndMinute(), dl.toWeeks(), out.getKey()));
        }

        for (Mac m : hostService.listMacByHost(hostId)) {
            if (m.getState() == Mac.State.ENABLE) {
                lines.add(String.format("iptables -A FORWARD -m mac --mac-source %s -j ACCEPT", m.getSerial()));
            }
        }
        //NAT
        lines.add("iptables -t nat -F");
        lines.add("iptables -t nat -X");
        lines.add("iptables -t nat -P PREROUTING ACCEPT");
        lines.add("iptables -t nat -P POSTROUTING ACCEPT");
        lines.add("iptables -t nat -P OUTPUT ACCEPT");
        for (Nat nat : hostService.listFirewallNat(hostId)) {
            lines.add(String.format("iptables -t nat -A PREROUTING -d %s -p %s --dport %d -j DNAT --to-destination %s.%d:%d",
                    wanIp.getAddress(), nat.getProtocol(), nat.getsPort(), host.getLanNet(), nat.getdIp(), nat.getdPort()));
            lines.add(String.format("iptables -t nat -A POSTROUTING -s %s.0/24 -d %s.%d -p %s --dport %s -j SNAT --to-source %s.1",
                    host.getLanNet(), host.getLanNet(), nat.getdIp(), nat.getProtocol(), nat.getdPort(), host.getLanNet()));
            lines.add(String.format("iptables -A FORWARD -d %s:%d -p %s --dport %s -j ACCEPT",
                    host.getLanNet(), nat.getdIp(), nat.getProtocol(), nat.getdPort()));
        }
        lines.add("iptables -A FORWARD  -m state --state ESTABLISHED -j ACCEPT");
        lines.add(String.format("iptables -t nat -A POSTROUTING -s %s.0/24 -o wan -j MASQUERADE", host.getLanNet()));
        //DMZ
        if (host.isDmz()) {
            // TODO 未验证
            for (Dmz dmz : hostService.listFirewallDmz(hostId)) {
                Ip dmzIp = hostService.getIp(dmz.getWanIp());
                lines.add(String.format("iptables -t nat -A PREROUTING -d %s -j DNAT --to-destination %s.1", dmzIp.getAddress(), host.getDmzNet()));
                lines.add(String.format("iptables -t nat -A POSTROUTING -s %s.1 -j SNAT --to-source %s", host.getDmzNet(), dmzIp.getAddress()));

            }

            lines.add(String.format("iptables -A FORWARD -s %s.1 -j ACCEPT", host.getDmzNet()));
            lines.add(String.format("iptables -A FORWARD -d %s.1 -j ACCEPT", host.getDmzNet()));
        }

        return lines;
    }

    public List<String> ffClear(long hostId) {
        Host host = hostService.getHost(hostId);
        List<String> lines = ffPrepare();
        lines.add("iptables -F");
        lines.add("iptables -X");
        lines.add("iptables -t nat -F");
        lines.add("iptables -t nat -X");
        lines.add("iptables -P INPUT DROP");
        lines.add("iptables -P OUTPUT ACCEPT");
        lines.add("iptables -P FORWARD ACCEPT");
        lines.add(String.format("iptables -t nat -A POSTROUTING -o wan -s %s.0/24 -j MASQUERADE", host.getLanNet()));
        if (host.isDmz()) {
            lines.add(String.format("iptables -t nat -A POSTROUTING -o wan -s %s.0/24 -j MASQUERADE", host.getDmzNet()));
        }
        //ICMP
        for (String p : new String[]{"0", "3", "3/4", "4", "11", "12", "14", "16", "18"}) {
            lines.add(String.format("iptables -A INPUT -i wan -p icmp --icmp-type %s -j ACCEPT", p));
        }
        if (host.isPing()) {
            lines.add("iptables -A INPUT -i wan -p icmp --icmp-type 8 -j ACCEPT");
        }
        //防火墙放开的TCP端口
        for (int i : new int[]{22, host.getRpcPort()}) {
            lines.add(String.format("iptables -A INPUT -p TCP -i wan --dport %d -j ACCEPT", i));
        }
        //LOOP
        lines.add("iptables -A INPUT -i lan -j ACCEPT");
        lines.add("iptables -A INPUT -m state --state ESTABLISHED -j ACCEPT");


        return lines;
    }

    public List<String> ffStatus() {
        List<String> lines = new ArrayList<>();
        lines.add("iptables -n -L");
        lines.add("iptables -t nat -n -L");
        return lines;
    }

    public List<String> tcApply(long hostId) {
        Host host = hostService.getHost(hostId);

        List<String> lines = new ArrayList<>();
        //清空打标信息
        lines.add("iptables -t mangle -F");
        lines.add("iptables -t mangle -X");
        //清空规则
        lines.add("tc qdisc del dev wan root 2>/dev/null");
        lines.add("tc qdisc del dev lan root 2>/dev/null");
        //顶层队列
        lines.add("tc qdisc add dev wan root handle 10: htb default 256");
        lines.add("tc qdisc add dev lan root handle 10: htb default 256");
        //第一层
        lines.add("tc class add dev wan parent 10: classid 10:1 htb rate 200mbit ceil 200mbit");
        lines.add("tc class add dev lan parent 10: classid 10:1 htb rate 500mbit ceil 500mbit");

        for (Mac m : hostService.listMacByHost(hostId)) {
            if (m.getState() == Mac.State.ENABLE) {
                FlowLimit f = hostService.getFirewallFlowLimit(m.getFlowLimit());
                //限速打标
                lines.add(String.format("iptables -t mangle -A PREROUTING  -s %s.%d -j MARK --set-mark 2%03d", host.getLanNet(), m.getIp(), m.getIp()));
                lines.add(String.format("iptables -t mangle -A PREROUTING  -s %s.%d -j RETURN", host.getLanNet(), m.getIp()));
                lines.add(String.format("iptables -t mangle -A POSTROUTING -d %s.%s -j MARK --set-mark 2%03d", host.getLanNet(), m.getIp(), m.getIp()));
                lines.add(String.format("iptables -t mangle -A POSTROUTING -d %s.%d -j RETURN", host.getLanNet(), m.getIp()));
                //限速规则
                lines.add(String.format("tc class add dev wan parent 10:1 classid 10:2%03d htb rate %dkbps ceil %dkbps prio 1",
                        m.getIp(), f.getUpRate(), f.getUpCeil()));
                lines.add(String.format("tc qdisc add dev wan parent 10:2%03d handle 100%03d: pfifo",
                        m.getIp(), m.getIp()));
                lines.add(String.format("tc filter add dev wan parent 10: protocol ip prio 100 handle 2%03d fw classid 10:2%03d",
                        m.getIp(), m.getIp()));
                lines.add(String.format("tc class add dev lan parent 10:1 classid 10:2%03d htb rate %dkbps ceil %dkbps prio 1",
                        m.getIp(), f.getDownRate(), f.getDownCeil()));
                lines.add(String.format("tc qdisc add dev lan parent 10:2%03d handle 100%03d: pfifo",
                        m.getIp(), m.getIp()));
                lines.add(String.format("tc filter add dev lan parent 10: protocol ip prio 100 handle 2%03d fw classid 10:2%03d",
                        m.getIp(), m.getIp()));

            }
        }

        return lines;
    }

    public List<String> tcStatus() {
        List<String> lines = new ArrayList<>();

        lines.add("iptables -t mangle -n -L");
        lines.add("tc -s -d qdisc show dev wan");
        lines.add("tc -s -d class show dev wan");
        lines.add("tc -s -d qdisc show dev lan");
        lines.add("tc -s -d class show dev lan");
        return lines;
    }

    public List<String> tcClear() {
        List<String> lines = new ArrayList<>();
        lines.add("iptables -t mangle -F");
        lines.add("iptables -t mangle -X");
        lines.add("tc qdisc del dev wan root");
        lines.add("tc qdisc del dev lan root");
        return lines;

    }

    public List<EtcFile> bind9Profile(long hostId) {
        Host host = hostService.getHost(hostId);


        List<EtcFile> etcs = new ArrayList<>();
        StringBuilder sbR = new StringBuilder();
        sbR.append("options {");
        sbR.append("directory \"/var/named\";");
        sbR.append("pid-file \"/var/run/named/named.pid\";");
        sbR.append("datasize default;");
        sbR.append(String.format("listen-on {%s.1;};", host.getLanNet()));
        sbR.append("forward only;");
        sbR.append("forwarders {8.8.8.8;8.8.4.4;};");
        sbR.append("allow-query { any; };");
        sbR.append("};");
        sbR.append(String.format("controls { inet %s.1 port 953 allow { localhost; }; };", host.getLanNet()));
        sbR.append("zone \"localhost\" IN {type master;file \"localhost.zone\";allow-transfer { any; };};");
        sbR.append("zone \"0.0.127.in-addr.arpa\" IN {type master;file \"127.0.0.zone\";allow-transfer { any; };};");
        sbR.append("zone \".\" IN {type hint;file \"root.hint\";};");
        sbR.append("logging {");
        sbR.append("channel xfer-log {file \"/var/log/named.log\";print-category yes;print-severity yes;print-time yes;severity info;};");
        sbR.append("category xfer-in { xfer-log; };");
        sbR.append("category xfer-out { xfer-log; };");
        sbR.append("category notify { xfer-log; };");
        sbR.append("};");
        for (Zone zone : hostService.listDnsZone(hostId)) {
            sbR.append(String.format("zone \"%s\" IN {", zone.getName()));
            sbR.append("type master;");
            sbR.append(String.format("file \"%s.zone\";", zone.getName()));
            sbR.append("allow-update {none;};");
            sbR.append("};");
        }

        etcs.add(new EtcFile("/etc/named.conf", "root:named", "440", sbR.toString()));

        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        for (Zone zone : hostService.listDnsZone(hostId)) {
            StringBuilder sb = new StringBuilder();
            List<Domain> domains = hostService.listDnsDomainByZone(zone.getId());
            sb.append("$TTL 86400\n");
            sb.append(String.format("@	IN SOA	%s. ns.%s. (\n", zone.getName(), zone.getName()));
            sb.append(String.format("%s\n", df.format(new Date())));
            sb.append("3H\n");
            sb.append("15M\n");
            sb.append("1W\n");
            sb.append("1D\n");
            sb.append(" )\n");
            for (Domain d : domains) {
                if (d.getType() == Domain.Type.NS) {
                    sb.append(String.format("\tIN NS	%s.%s.\n", d.getName(), zone.getName()));
                }
            }

            for (Domain d : domains) {
                if (d.getType() == Domain.Type.MX) {
                    sb.append(String.format("\tIN MX %d %s.%s.\n", d.getPriority(), d.getName(), zone.getName()));
                }
            }
            sb.append(String.format("ns\tIN A\t%s.1\n", host.getLanNet()));
            for (Domain d : domains) {
                sb.append(String.format("%s\tIN A\t%s\n",
                        d.getName(),
                        d.isLocal() ? String.format("%s.%d", host.getLanNet(), d.getLanIp()) : d.getWanIp()));
            }
            etcs.add(new EtcFile("/var/named/" + zone.getName() + ".zone", "root:named", "440", sb.toString()));
        }

        return etcs;
    }

    public EtcFile dhcpdProfile(long hostId) {
        Host host = hostService.getHost(hostId);


        StringBuilder sb = new StringBuilder();
        sb.append(String.format("option domain-name \"%s\";", host.getDomain()));
        sb.append("default-lease-time 600;");
        sb.append("max-lease-time 7200;");
        sb.append("authoritative;");
        sb.append("log-facility local7;");
        sb.append(String.format("subnet %s.0 netmask %s.0 {", host.getLanNet(), host.getLanNet()));
        sb.append(String.format("range dynamic-bootp %s.2  %s.254;", host.getLanNet(), host.getLanNet()));
        sb.append(String.format("option broadcast-address %s.255;", host.getLanNet()));
        sb.append(String.format("option routers %s.1;", host.getLanNet()));
        sb.append(String.format("option domain-name-servers %s.1;", host.getLanNet()));
        sb.append("}");
        for (Mac m : hostService.listMacByHost(hostId)) {
            if (m.getState() == Mac.State.ENABLE && m.isBind()) {
                sb.append(String.format("host pc-%03d {", m.getIp()));
                sb.append(String.format("hardware ethernet %s;", m.getSerial()));
                sb.append(String.format("fixed-address %s.%d;", host.getLanNet(), m.getIp()));
                sb.append("}");
            }
        }

        return new EtcFile("/etc/dhcpd.conf", "root:root", "444", sb.toString());
    }

    private List<String> ffPrepare() {
        List<String> lines = new ArrayList<>();
        //内核模块
        for (String m : new String[]{"ip_tables", "iptable_nat", "ip_nat_ftp", "ip_nat_irc", "ip_conntrack", "ip_conntrack_ftp", "ip_conntrack_irc", "ip_conntrack_netbios_ns"}) {
            lines.add(String.format("modprobe -v %s", m));
        }
        //配置文件
        Map<String, Integer> proc = new HashMap<>();
        proc.put("/proc/sys/net/ipv4/ip_forward", 1);
        proc.put("/proc/sys/net/ipv4/tcp_syncookies", 1);
        proc.put("/proc/sys/net/ipv4/icmp_echo_ignore_broadcasts", 1);
        for (String s : new String[]{"default", "all", "wan", "lan", "lo"}) {
            proc.put("/proc/sys/net/ipv4/conf/" + s + "/rp_filter", 1);
            proc.put("/proc/sys/net/ipv4/conf/" + s + "/log_martians", 1);
            proc.put("/proc/sys/net/ipv4/conf/" + s + "/accept_source_route", 0);
            proc.put("/proc/sys/net/ipv4/conf/" + s + "/accept_redirects", 0);
            proc.put("/proc/sys/net/ipv4/conf/" + s + "/send_redirects", 0);
        }
        for (String f : proc.keySet()) {
            lines.add(String.format("echo \"%d\" > %s", proc.get(f), f));
        }

        return lines;
    }

    @Resource
    private SiteService siteService;
    @Resource
    private HostService hostService;
    @Resource
    private EncryptHelper encryptHelper;
    @Resource
    private JsonHelper jsonHelper;
    @Value("${lan.net.extends}")
    private String lanNetExtends;

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setLanNetExtends(String lanNetExtends) {
        this.lanNetExtends = lanNetExtends;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }
}
