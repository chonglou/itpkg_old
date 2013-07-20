package com.odong.itpkg.net;

import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.util.CommandHelper;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.FileHelper;
import com.sun.management.OperatingSystemMXBean;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:00
 */
public class Handler extends SimpleChannelInboundHandler<Rpc.Request> {

    public Handler(EncryptHelper encryptHelper) {
        super();
        this.encryptHelper = encryptHelper;
        this.debug = !"root".equals(System.getProperty("user.name"));
    }


    @Override
    public void channelRead0(ChannelHandlerContext ctx, Rpc.Request request) throws Exception {

        logger.debug("请求：\n{}", request);
        Rpc.Response response = process(request);
        logger.debug("返回：\n{}", response);
        ChannelFuture cf = ctx.write(response);
        if (response.getType() == Rpc.Type.BYE) {
            cf.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("网络错误", cause);
        ctx.close();
    }

    private Rpc.Response process(Rpc.Request request) {

        Rpc.Type type = request.getType();
        Rpc.Code code = Rpc.Code.FAIL;
        List<String> lines = new ArrayList<>();

        switch (request.getType()) {
            case HEART:
                Properties props = System.getProperties();
                for (Object t : props.keySet()) {
                    lines.add(t + "=" + props.getProperty(t.toString()));
                }
                OperatingSystemMXBean mxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                lines.add("memory.status=" + mxBean.getFreePhysicalMemorySize() + "/" + mxBean.getTotalPhysicalMemorySize());
                lines.add("swap.status=" + mxBean.getFreeSwapSpaceSize() + "/" + mxBean.getTotalSwapSpaceSize());
                lines.add("cpu.system=" + mxBean.getSystemCpuLoad());
                lines.add("cpu.process=" + mxBean.getProcessCpuLoad());
                code = Rpc.Code.SUCCESS;
                break;
            case COMMAND:
                try {
                    if (debug) {
                        logger.debug("命令：", getLines(request));
                    } else {
                        for (String s : CommandHelper.execute(getLines(request).toArray(new String[1]))) {
                            lines.add(encryptHelper.encode(s));
                        }
                    }
                    code = Rpc.Code.SUCCESS;
                } catch (Exception e) {
                    code = Rpc.Code.FAIL;
                    lines.add("异常：" + e.getMessage());
                }

                break;
            case FILE:
                String filename = request.getName();
                if (debug) {
                    filename = "/tmp" + request.getName();
                }
                try {

                    FileHelper.write(filename, getLines(request).toArray(new String[1]));
                    for (String s : CommandHelper.execute(
                            String.format("chown %s %s", request.getOwner(), filename),
                            String.format("chmod %s %s", request.getMode(), filename))) {
                        lines.add(s);
                    }
                    code = Rpc.Code.SUCCESS;
                } catch (Exception e) {
                    code = Rpc.Code.FAIL;
                    lines.add("异常：" + e.getMessage());
                }
                break;
            case BYE:
                code = Rpc.Code.SUCCESS;
                break;
            default:
                logger.error("未知的请求：", request.getType());
                type = Rpc.Type.BYE;
                lines.add("未知的请求[" + request.getType() + "]");
                break;
        }

        return builder(type, code, lines).build();
    }

    private List<String> getLines(Rpc.Request request){
        List<String> lines = new ArrayList<>();
        for(String line : request.getLinesList()){
            lines.add(encryptHelper.decode(line));
        }
        return lines;
    }
    private Rpc.Response.Builder builder(Rpc.Type type, Rpc.Code code, List<String> list) {
        Rpc.Response.Builder builder = Rpc.Response.newBuilder();
        if (list != null) {
            for (String s : list) {
                builder.addLines(encryptHelper.encode(s));
            }
        }
        return builder.setType(type).setCode(code).setCreated(new Date().getTime());
    }

    private final static Logger logger = LoggerFactory.getLogger(Handler.class);
    private boolean debug;
    private EncryptHelper encryptHelper;

}
