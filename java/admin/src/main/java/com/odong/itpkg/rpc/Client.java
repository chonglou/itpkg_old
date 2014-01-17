package com.odong.itpkg.rpc;

import com.odong.itpkg.model.Rpc;
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

import java.util.Date;


/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-8-2
 * Time: 上午10:00
 */
public final class Client {

    public String decode(String encrypt) {
        return ste.decrypt(encrypt);
    }

    public Rpc.Request command(String... lines) {
        return buildRequest(Rpc.Type.COMMAND, lines).build();
    }

    public Rpc.Request file(String name, String owner, String mode, String... lines) {
        return buildRequest(Rpc.Type.FILE, lines).setName(name).setOwner(owner).setMode(mode).build();
    }

    public Rpc.Request heart() {
        return buildRequest(Rpc.Type.HEART).build();
    }

    public Rpc.Request bye() {
        return buildRequest(Rpc.Type.BYE).build();
    }


    public Client(String host, int port, String key) {
        this.host = host;
        this.port = port;
        ste = new StrongTextEncryptor();
        ste.setPassword(key);

    }

    public Rpc.Response send(Rpc.Request request) {
        EventLoopGroup group = new NioEventLoopGroup();
        Rpc.Response.Builder builder = Rpc.Response.newBuilder();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new Initializer());
            logger.debug("建立连接[{}:{}]", host, port);
            Channel ch = bootstrap.connect(host, port).sync().channel();
            Handler handler = ch.pipeline().get(Handler.class);
            Rpc.Response response = handler.getResponse(request);
            ch.close();
            builder.setType(response.getType()).setCode(response.getCode()).setCreated(response.getCreated());
            for (String s : response.getLinesList()) {
                builder.addLines(ste.decrypt(s));
            }

        } catch (Exception e) {
            logger.error("网络出错", e);
            builder.setType(request.getType()).setCode(Rpc.Code.FAIL).setCreated(new Date().getTime());
            if (e instanceof EncryptionOperationNotPossibleException) {
                throw new IllegalArgumentException("密钥不对");
            }
            throw new IllegalArgumentException("网络错误");
        } finally {
            group.shutdownGracefully();
        }
        return builder.build();
    }


    private Rpc.Request.Builder buildRequest(Rpc.Type type, String... lines) {
        Rpc.Request.Builder builder = Rpc.Request.newBuilder();
        if (lines != null) {
            for (String line : lines) {
                builder.addLines(ste.encrypt(line));
            }
        }
        return builder.setType(type).setCreated(new Date().getTime());
    }


    private final StrongTextEncryptor ste;
    private final String host;
    private final int port;
    private final static Logger logger = LoggerFactory.getLogger(Client.class);
}
