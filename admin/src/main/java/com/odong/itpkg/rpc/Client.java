package com.odong.itpkg.rpc;

import com.odong.itpkg.model.Rpc;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.jasypt.util.text.StrongTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:39
 */
public class Client {
    public Client(String host, int port, String key, int signLength, Callback callback) {
        this.signLength = signLength;
        this.ste = new StrongTextEncryptor();
        ste.setPassword(key);
        this.group = new NioEventLoopGroup();
        this.random = new Random();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new Initializer(callback));

        try {
            rootCH = bootstrap.connect(host, port).sync().channel();
        } catch (InterruptedException e) {
            throw new IllegalArgumentException("连接服务器[" + host + ":" + port + "]错误", e);
        }
    }

    public Rpc.Request command(List<String> lines) {
        return builder(Rpc.Type.COMMAND).addAllLines(lines).build();
    }

    public Rpc.Request file(String name, String mode, List<String> lines) {
        return builder(Rpc.Type.FILE).setName(name).setMode(mode == null ? "rw-------" : mode).addAllLines(lines).build();
    }

    public Rpc.Request heart() {
        return builder(Rpc.Type.HEART).build();
    }

    public Rpc.Request bye() {
        return builder(Rpc.Type.BYE).build();
    }


    public void close() {
        if (lastCF != null) {
            try {
                lastCF.sync();
            } catch (InterruptedException e) {
                logger.error("关闭RPC Client", e);
            }
        }
        group.shutdownGracefully();
    }

    public void send(Rpc.Request request) {
        try {
            logger.debug("发送消息：\n{}", request);
            lastCF = rootCH.writeAndFlush(request);
            if (request.getType() == Rpc.Type.BYE) {
                rootCH.closeFuture().sync();
            }
        } catch (InterruptedException e) {
            logger.error("RPC失败");
            throw new RuntimeException(e);
        }
    }


    private String random(int len) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }

    private Rpc.Request.Builder builder(Rpc.Type type) {
        return Rpc.Request.newBuilder().setSign(ste.encrypt(random(signLength))).setType(type).setCreated(new Date().getTime());
    }


    private EventLoopGroup group;
    private int signLength;
    private StrongTextEncryptor ste;
    private Channel rootCH;
    private ChannelFuture lastCF;
    private Random random;

    private final static Logger logger = LoggerFactory.getLogger(Client.class);

}
