package com.odong.itpkg.rpc;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.util.EncryptHelper;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-1
 * Time: 上午11:52
 */
@Component
public class RpcHelper {

    public String decode(long hostId, String encrypt) {
        return getClient(hostId).decode(encrypt);
    }

    public Rpc.Response file(long hostId, String name, String owner, String mode, String... lines) {
        Client client = getClient(hostId);
        return client.send(client.file(name, owner, mode, lines));
    }

    public Rpc.Response command(long hostId, String... lines) {
        Client client = getClient(hostId);
        return client.send(client.command(lines));
    }


    public Rpc.Response heart(long hostId) {
        Client client = getClient(hostId);
        return client.send(client.heart());
    }


    public Rpc.Response bye(long hostId) {
        Client client = getClient(hostId);
        return client.send(client.bye());
    }

    public synchronized void pop(long hostId) {
        clientMap.remove(host2key(hostId));
    }


    @PostConstruct
    void init() {
        group = new NioEventLoopGroup();
        clientMap = new HashMap<>();
    }

    @PreDestroy
    void destroy() {
        clientMap.clear();
        group.shutdownGracefully();
    }


    private synchronized Client getClient(long hostId) {
        Client c = clientMap.get(host2key(hostId));
        if (c == null) {
            Host host = hostService.getHost(hostId);
            Ip wanIp = hostService.getIp(host.getWanIp());
            if (wanIp.getAddress() == null) {
                throw new IllegalArgumentException("主机[" + host + "]没有公网IP");
            }
            c = new Client(wanIp.getAddress(), host.getRpcPort(), encryptHelper.decode(host.getSignKey()));
            clientMap.put(host2key(hostId), c);
        }
        return c;
    }


    private Rpc.Response send(long hostId, Rpc.Request request) {
        return null;
    }

    private String host2key(long hostId) {
        return "host://" + hostId;
    }

    private EventLoopGroup group;
    private Map<String, Client> clientMap;
    @Resource
    private HostService hostService;
    @Resource
    private EncryptHelper encryptHelper;
    private final static Logger logger = LoggerFactory.getLogger(RpcHelper.class);

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }
}
