package com.odong.itpkg.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午5:29
 */

@Component
public class SSHHelper {


    public List<String> execute(String host, int port, String username, String password, String...commands){
        List<String> lines = new ArrayList<>();
        try{
            Session session = getSession(host,port,username,password);
            session.connect();
            for(String cmd : commands){
                logger.debug("{}@{}运行：{}", username, host,cmd);
                Channel channel = session.openChannel("exec");
                ((ChannelExec) channel).setCommand(cmd);
                channel.setInputStream(null);
                ((ChannelExec) channel).setErrStream(System.err);
                InputStream in = channel.getInputStream();

                channel.connect();

                byte[] tmp = new byte[BUF_LEN];
                while (true)
                {
                    while (in.available() > 0)
                    {
                        int i = in.read(tmp, 0, BUF_LEN);
                        if (i < 0){
                            break;
                        }
                        lines.add(new String(tmp, 0, i));
                    }
                    if (channel.isClosed())
                    {
                        logger.debug("{}@{}exit-status: {}", username, host, channel.getExitStatus());
                        break;
                    }

                }

                channel.disconnect();
            }
            session.disconnect();
        }
        catch (JSchException | IOException e){
            logger.error("SSH出错", e);
            lines.add(e.getMessage());
        }
        return lines;
    }



    private Session getSession(String host, int port, String username, String password) throws JSchException{

        Session session = jSch.getSession(username,host, port);
        session.setPassword(password);
        session.setConfig(config);
        return session;
    }


    @PostConstruct
    public void init(){
        config = new Properties();

        config.put("StrictHostKeyChecking", "no");
        config.put("compression.s2c", "zlib@openssh.com,zlib,none");
        config.put("compression.c2s", "zlib@openssh.com,zlib,none");
        config.put("compression_level", "9");

        jSch = new JSch();

    }

    private final int BUF_LEN=1024;
    private JSch jSch;
    private Properties config;
    private final static Logger logger = LoggerFactory.getLogger(SSHHelper.class);



}
