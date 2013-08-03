package com.odong.itpkg.net;

import com.odong.itpkg.util.EncryptHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:06
 */
@Component("rpc.server")
public class Server {

    @PreDestroy
    void destroy() {
        try {
            rootCF.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("停止服务出错", e);
        } finally {
            workerG.shutdownGracefully();
            bossG.shutdownGracefully();
        }
    }


    @PostConstruct
    void init() {
        bossG = new NioEventLoopGroup();
        workerG = new NioEventLoopGroup();
        ServerBootstrap sb = new ServerBootstrap();
        sb.group(bossG, workerG).channel(NioServerSocketChannel.class)
                .childHandler(new Initializer(encryptHelper))
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {

            rootCF = sb.bind(port).sync();
        } catch (InterruptedException e) {
            logger.error("启动服务出错", e);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(Server.class);


    private ChannelFuture rootCF;
    private EventLoopGroup bossG;
    private EventLoopGroup workerG;
    @Resource
    private EncryptHelper encryptHelper;
    @Value("${server.port}")
    private int port;

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
