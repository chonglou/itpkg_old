package com.odong.itpkg.rpc;

import com.odong.itpkg.model.Rpc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:43
 */
public class Handler extends SimpleChannelInboundHandler<Rpc.Response> {
    public Handler(Rpc.Request request) {
        super();
        this.request = request;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("发送消息", request);
        ctx.writeAndFlush(request);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Rpc.Response response) throws Exception {
        logger.debug("收到消息", response);
        this.response = response;
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("通道停止");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("网络异常", cause);
        ctx.close();
    }

    private final static Logger logger = LoggerFactory.getLogger(Handler.class);
    private Rpc.Request request;
    private Rpc.Response response;

    public Rpc.Response getResponse() {
        return response;
    }
}
