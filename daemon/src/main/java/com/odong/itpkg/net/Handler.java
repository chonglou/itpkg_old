package com.odong.itpkg.net;

import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.rpc.JsonHelper;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:00
 */
@Component("ctx.handler")
public class Handler extends SimpleChannelInboundHandler<Rpc.Request> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("建立连接");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Rpc.Request request) throws Exception {

        logger.debug("请求：{}", jsonHelper.object2json(request));
        Rpc.Response response = process(request);
        logger.debug("返回：{}", jsonHelper.object2json(response));
        ChannelFuture cf = ctx.write(response);
        if(response.getType() == Rpc.Type.BYE){
            cf.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("网络异常", cause);
        ctx.close();
    }

    private Rpc.Response process(Rpc.Request request) {
        Rpc.Response.Builder response = Rpc.Response.newBuilder().setType(request.getType()).setCode(Rpc.Code.FAIL);
        switch (request.getType()) {
            case COMMAND:
                try {
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
                    response.setCode(Rpc.Code.SUCCESS);
                } catch (InterruptedException | IOException e) {
                    response.addLines(e.getMessage());
                    break;
                }

                break;
            case FILE:
                Path file = Paths.get(request.getName());
                if (request.getMode() != null) {
                    try {
                        Files.deleteIfExists(file);
                        Files.createFile(file, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(request.getMode())));
                    } catch (IOException e) {
                        logger.error("创建文件失败", e);
                        response.addLines(e.getMessage());
                        break;
                    }
                }

                try (
                        BufferedWriter writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"))) {
                    for (String s : request.getLinesList()) {
                        writer.write(s, 0, s.length());
                    }
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
                break;

        }
        return response.setCreated(new Date().getTime()).build();
    }

    private final static Logger logger = LoggerFactory.getLogger(Handler.class);
    @Resource
    private JsonHelper jsonHelper;

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }
}
