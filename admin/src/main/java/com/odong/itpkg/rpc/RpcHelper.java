package com.odong.itpkg.rpc;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.util.EncryptHelper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.Date;
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

    public Rpc.Response file(long hostId, String name, String owner, String mode, String... lines) {
        return send(hostId, getFileRequest(hostId, name, owner, mode, lines));
    }

    public void file(long hostId, String name, String owner, String mode, String[] lines, Callback callback) {
        send(hostId, getFileRequest(hostId, name, owner, mode, lines), callback);
    }

    public Rpc.Response command(long hostId, String... commands) {
        return send(hostId, getCommandRequest(hostId, commands));
    }

    public void command(long hostId, String[] commands, Callback callback) {
        send(hostId, getCommandRequest(hostId, commands), callback);
    }

    public Rpc.Response heart(long hostId) {
        return send(hostId, getHeartRequest(hostId));
    }

    public void heart(long hostId, Callback callback) {
        send(hostId, getHeartRequest(hostId), callback);
    }

    public Rpc.Response bye(long hostId) {
        return send(hostId, getByeRequest(hostId));
    }

    public void bye(long hostId, Callback callback) {
        send(hostId, getByeRequest(hostId), callback);
    }

    public synchronized void pop(long hostId) {
        clientMap.remove(host2key(hostId));
    }

    public String decode(long hostId, String encrypt) {

        return getConnection(hostId).ste.decrypt(encrypt);
    }

    public Rpc.Response decode(long hostId, Rpc.Response response) {
        Rpc.Response.Builder builder = Rpc.Response.newBuilder().setCode(response.getCode()).setType(response.getType()).setCreated(response.getCreated());
        for (String s : response.getLinesList()) {
            builder.addLines(getConnection(hostId).ste.decrypt(s));
        }
        return builder.build();
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


    private synchronized Connection getConnection(long hostId) {
        Connection c = clientMap.get(host2key(hostId));
        if (c == null) {
            Host host = hostService.getHost(hostId);
            Ip wanIp = hostService.getIp(host.getWanIp());
            if (wanIp.getAddress() == null) {
                throw new IllegalArgumentException("主机[" + host + "]没有公网IP");
            }
            c = new Connection(wanIp.getAddress(), host.getRpcPort(), encryptHelper.decode(host.getSignKey()));
            clientMap.put(host2key(hostId), c);
        }
        return c;
    }


    private Rpc.Request getCommandRequest(long hostId, String... lines) {
        return buildRequest(hostId, Rpc.Type.COMMAND, lines).build();
    }

    private Rpc.Request getFileRequest(long hostId, String name, String owner, String mode, String... lines) {
        return buildRequest(hostId, Rpc.Type.FILE, lines).setName(name).setOwner(owner).setMode(mode).build();
    }

    private Rpc.Request getHeartRequest(long hostId) {
        return buildRequest(hostId, Rpc.Type.HEART).build();
    }

    private Rpc.Request getByeRequest(long hostId) {
        return buildRequest(hostId, Rpc.Type.BYE).build();
    }

    private Rpc.Request.Builder buildRequest(long hostId, Rpc.Type type, String... lines) {
        Connection c = getConnection(hostId);
        Rpc.Request.Builder builder = Rpc.Request.newBuilder();
        if (lines != null) {
            for (String line : lines) {
                builder.addLines(c.ste.encrypt(line));
            }
        }
        return builder.setType(type).setCreated(new Date().getTime());
    }

    private void send(long hostId, Rpc.Request request, Callback callback) {
        Connection c = getConnection(hostId);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new Initializer(callback));

        try {
            logger.debug("建立连接[{}:{}]", c.host, c.port);
            Channel rootCH = bootstrap.connect(c.host, c.port).sync().channel();
            logger.debug("发送消息：\n{}", request);
            rootCH.writeAndFlush(request).sync();

            rootCH.closeFuture().sync();

        } catch (InterruptedException e) {
            throw new IllegalArgumentException("连接服务器[" + c.host + ":" + c.port + "]错误", e);
        }
    }

    private Rpc.Response send(long hostId, Rpc.Request request) {
        //TODO 阻塞模式出错
        final Rpc.Response.Builder builder = Rpc.Response.newBuilder();
        final Connection c = getConnection(hostId);
        send(hostId, request, new Callback() {
            @Override
            public void execute(Rpc.Response response) {
                try {
                    for (String s : response.getLinesList()) {
                        builder.addLines(c.ste.decrypt(s));
                    }
                } catch (EncryptionOperationNotPossibleException e) {
                    builder.addLines("密钥不对");
                } catch (Exception e) {
                    builder.addLines(e.getMessage());
                }
                builder.setCode(response.getCode());
                builder.setType(response.getType());
                builder.setCreated(response.getCreated());
            }
        });

        for (int i = 0; i < 5 && builder.getCreated() == 0; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (builder.getCreated() == 0) {
            throw new RuntimeException("网络错误");
        }
        return builder.build();
    }

    private String host2key(long hostId) {
        return "host://" + hostId;
    }

    private EventLoopGroup group;
    private Map<String, Connection> clientMap;
    @Resource
    private HostService hostService;
    @Resource
    private EncryptHelper encryptHelper;
    private final static Logger logger = LoggerFactory.getLogger(RpcHelper.class);

    private class Connection {
        Connection(String host, int port, String key) {
            this.host = host;
            this.port = port;
            this.ste = new StrongTextEncryptor();
            ste.setPassword(key);
        }

        private final String host;
        private final int port;
        private final StrongTextEncryptor ste;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }
}
