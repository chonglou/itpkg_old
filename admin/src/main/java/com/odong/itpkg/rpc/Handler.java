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
    public Handler(Callback callback) {
        super();
        this.callback = callback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Rpc.Response response) throws Exception {
        logger.debug("收到消息：\n{}", response);
        callback.execute(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("网络出错", cause);
        ctx.close();
    }

    private final static Logger logger = LoggerFactory.getLogger(Handler.class);
    private Callback callback;
}
