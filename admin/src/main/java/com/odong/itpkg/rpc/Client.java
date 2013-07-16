package com.odong.itpkg.rpc;

import com.odong.itpkg.model.Rpc;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:39
 */
@Component("rpc.client")
public class Client {
    public Rpc.Response call(String host, int port, Rpc.Request request) {

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class).option(ChannelOption.SO_KEEPALIVE, true).handler(new Initializer(request));

            ChannelFuture cf = bootstrap.connect(host, port).sync();
            return ((Handler) cf.channel().pipeline().last()).getResponse();
        } catch (InterruptedException e) {
            logger.error("RPC失败");
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    void init() {
        group = new NioEventLoopGroup();
    }

    @PreDestroy
    void destroy() {
        group.shutdownGracefully();
    }

    @Resource
    private JsonHelper jsonHelper;
    private EventLoopGroup group;
    private final static Logger logger = LoggerFactory.getLogger(Client.class);

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }
}
