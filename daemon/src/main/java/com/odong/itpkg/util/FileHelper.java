package com.odong.itpkg.util;

import com.odong.itpkg.model.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午1:33
 */
public final class FileHelper {
    private FileHelper(){}
    public static boolean exist(String name){
        return Files.isRegularFile(Paths.get(name));
    }
    public static void write(String name, String mode, String... lines){
        try {
            Path file = Paths.get(name);
            if (!Files.isDirectory(file.getParent())) {
                Files.createDirectories(file.getParent());
            }
            Files.deleteIfExists(file);
            Files.createFile(file, PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString(mode)));

            BufferedWriter writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"));

            for (String s : lines) {
                writer.write(s, 0, s.length());
                writer.write('\n');
            }

            writer.close();

        } catch (IOException e) {
            logger.error("写文件出错", e);
            throw new RuntimeException(e);
        }


    }
    private final static Logger logger = LoggerFactory.getLogger(FileHelper.class);
}
