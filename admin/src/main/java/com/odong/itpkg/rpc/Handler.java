package com.odong.itpkg.rpc;

import com.odong.itpkg.model.Rpc;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:43
 */
public class Handler extends SimpleChannelInboundHandler<Rpc.Response> {
    public Handler() {
        super(false);
    }

    public Rpc.Response getResponse(Rpc.Request request) {
        channel.writeAndFlush(request);
        Rpc.Response response;
        boolean interrupted = false;
        for (; ; ) {
            try {
                response = answer.take();
                break;
            } catch (InterruptedException e) {
                interrupted = true;
            }
        }
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        return response;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Rpc.Response response) throws Exception {
        logger.debug("收到消息：\n{}", response);
        answer.add(response);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("网络出错", cause);
        ctx.close();
    }

    private volatile Channel channel;
    private final BlockingQueue<Rpc.Response> answer = new LinkedBlockingDeque<>();
    private final static Logger logger = LoggerFactory.getLogger(Handler.class);

}
