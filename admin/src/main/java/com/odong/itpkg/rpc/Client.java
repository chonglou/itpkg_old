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

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:39
 */
public class Client {
    public Client(String key) {
        this.ste = new StrongTextEncryptor();
        ste.setPassword(key);
        this.group = new NioEventLoopGroup();


    }


    public Rpc.Request command(List<String> lines) {
        return builder(Rpc.Type.COMMAND, lines).build();
    }

    public Rpc.Request file(String name, String owner, String mode, List<String> lines) {
        return builder(Rpc.Type.FILE, lines).setName(name).setOwner(owner).setMode(mode).build();
    }

    public Rpc.Request heart() {
        return builder(Rpc.Type.HEART, null).build();
    }

    public Rpc.Request bye() {
        return builder(Rpc.Type.BYE, null).build();
    }

    public String decode(String encrypt) {
        return ste.decrypt(encrypt);
    }


    public void open(String host, int port, Callback callback) {
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


    private Rpc.Request.Builder builder(Rpc.Type type, List<String> lines) {
        Rpc.Request.Builder builder = Rpc.Request.newBuilder();
        if (lines != null) {
            for (String line : lines) {
                builder.addLines(ste.encrypt(line));
            }
        }
        return builder.setType(type).setCreated(new Date().getTime());
    }


    private EventLoopGroup group;
    private StrongTextEncryptor ste;
    private Channel rootCH;
    private ChannelFuture lastCF;

    private final static Logger logger = LoggerFactory.getLogger(Client.class);

}
