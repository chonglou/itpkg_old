package com.odong.itpkg.net;

import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import com.sun.management.OperatingSystemMXBean;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:00
 */
public class Handler extends SimpleChannelInboundHandler<Rpc.Request> {

    public Handler(JsonHelper jsonHelper, EncryptHelper encryptHelper, int signLength) {
        super();
        this.jsonHelper = jsonHelper;
        this.encryptHelper = encryptHelper;
        this.signLength = signLength;
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
        Rpc.Response.Builder response = Rpc.Response.newBuilder().setType(request.getType()).setCode(Rpc.Code.FAIL);
        if (encryptHelper.decode(request.getSign()).length() == signLength) {
            switch (request.getType()) {
                case HEART:
                    Properties props = System.getProperties();
                    for (Object t : props.keySet()) {
                        response.addLines(t + "=" + props.getProperty(t.toString()));
                    }
                    OperatingSystemMXBean mxBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                    response.addLines("memory.status=" + mxBean.getFreePhysicalMemorySize() + "/" + mxBean.getTotalPhysicalMemorySize());
                    response.addLines("swap.status=" + mxBean.getFreeSwapSpaceSize() + "/" + mxBean.getTotalSwapSpaceSize());
                    response.addLines("cpu.system=" + mxBean.getSystemCpuLoad());
                    response.addLines("cpu.process=" + mxBean.getProcessCpuLoad());
                    response.setCode(Rpc.Code.SUCCESS);
                    break;
                case COMMAND:
                    try {
                        if (debug) {
                            for (String s : request.getLinesList()) {
                                logger.debug(s);
                            }
                        } else {
                            for (String s : request.getLinesList()) {
                                Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", s}, null, null);
                                LineNumberReader reader = new LineNumberReader(new InputStreamReader(process.getInputStream()));

                                String line;
                                process.waitFor();
                                while ((line = reader.readLine()) != null) {
                                    response.addLines(line);
                                }
                                reader.close();
                            }
                        }
                        response.setCode(Rpc.Code.SUCCESS);
                    } catch (InterruptedException | IOException e) {
                        logger.error("执行命令出错",e.getMessage());
                        response.addLines(e.getMessage());
                        break;
                    }

                    break;
                case FILE:
                    String filename = request.getName();
                    if (debug) {
                        filename = "/tmp" + request.getName();
                    }
                    try {
                        Path file = Paths.get(filename);
                        Files.deleteIfExists(file);
                        Files.createFile(file, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(request.getMode())));
                        BufferedWriter writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"));

                        for (String s : request.getLinesList()) {
                            writer.write(s, 0, s.length());
                        }
                        writer.close();

                        response.setCode(Rpc.Code.SUCCESS);
                    } catch (IOException e) {
                        logger.error("写文件出错", e);
                        response.addLines(e.getMessage());
                    }


                    break;
                case BYE:
                    response.setCode(Rpc.Code.SUCCESS);
                    break;
                default:
                    logger.error("未知的请求：", request.getType());
                    response.setType(Rpc.Type.BYE);
                    response.addLines("未知的请求[" + response.getType() + "]");
                    break;

            }
        } else {
            logger.error("签名不对");
            response.setType(Rpc.Type.BYE);
            response.addLines("签名不对");
        }
        return response.setCreated(new Date().getTime()).build();
    }

    private final static Logger logger = LoggerFactory.getLogger(Handler.class);
    private boolean debug;
    private JsonHelper jsonHelper;
    private EncryptHelper encryptHelper;
    private int signLength;

}
