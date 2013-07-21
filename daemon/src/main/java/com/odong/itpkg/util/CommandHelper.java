package com.odong.itpkg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午1:57
 */

public final class CommandHelper {
    private CommandHelper() {
    }

    public static List<String> execute(String... commands) {
        List<String> lines = new ArrayList<>();
        try {
            for (String cmd : commands) {
                logger.debug("运行：{}", cmd);
                Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", cmd}, null, null);
                LineNumberReader out = new LineNumberReader(new InputStreamReader(process.getInputStream()));
                LineNumberReader err = new LineNumberReader(new InputStreamReader(process.getErrorStream()));

                String line;
                int code = process.waitFor();

                if (code == 0) {
                    while ((line = out.readLine()) != null) {
                        logger.debug(line);
                        lines.add(line);
                    }
                } else {
                    while ((line = err.readLine()) != null) {
                        logger.debug(line);
                        lines.add(line);
                    }
                    throw new IllegalArgumentException("出错，返回值[" + code + "]");
                }
                out.close();
                err.close();
            }

        } catch (InterruptedException | IOException e) {
            logger.error("出错", e.getMessage());
            lines.add(e.getMessage());
            throw new RuntimeException(e);
        }
        return lines;
    }

    private final static Logger logger = LoggerFactory.getLogger(CommandHelper.class);

}
