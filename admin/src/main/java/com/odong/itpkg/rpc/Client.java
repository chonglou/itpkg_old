package com.odong.itpkg.rpc;

import com.odong.itpkg.Constant;
import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.util.JsonHelper;
import com.odong.itpkg.util.StringHelper;
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
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:39
 */
@Component("rpc.client")
public class Client {
    public Rpc.Request command(List<String> lines) {
        return builder(Rpc.Type.COMMAND).addAllLines(lines).build();
    }

    public Rpc.Request file(String name, String mode, List<String> lines) {
        return builder(Rpc.Type.FILE).setName(name).setMode(mode == null ? "444" : mode).addAllLines(lines).build();
    }

    public Rpc.Request heart() {
        return builder(Rpc.Type.HEART).setType(Rpc.Type.HEART).build();
    }

    private Rpc.Request.Builder builder(Rpc.Type type) {
        return Rpc.Request.newBuilder().setType(type).setCreated(new Date().getTime()).setSign(stringHelper.random(Constant.SIGN_LENGTH));
    }

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

    private EventLoopGroup group;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private StringHelper stringHelper;

    public void setStringHelper(StringHelper stringHelper) {
        this.stringHelper = stringHelper;
    }

    private final static Logger logger = LoggerFactory.getLogger(Client.class);

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }
}
