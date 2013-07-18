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
            for (String s : commands) {
                Process process = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", s}, null, null);
                LineNumberReader reader = new LineNumberReader(new InputStreamReader(process.getInputStream()));

                String line;
                process.waitFor();
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                reader.close();
            }

        } catch (InterruptedException | IOException e) {
            logger.error("执行命令出错", e.getMessage());
            lines.add(e.getMessage());
            throw new RuntimeException(e);
        }
        return lines;
    }

    private final static Logger logger = LoggerFactory.getLogger(CommandHelper.class);

}
