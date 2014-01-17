package com.odong.portal.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.UnsupportedOptionsException;
import org.tukaani.xz.XZOutputStream;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-4
 * Time: 下午2:36
 */
@Component
public class ZipHelper {
    public void compress(String input, boolean delete) throws IOException {
        try (
                FileInputStream in = new FileInputStream(input);
                XZOutputStream out = new XZOutputStream(new FileOutputStream(input + ".xz"), options);
        ) {
            byte[] buf = new byte[8192];
            int size;
            while ((size = in.read(buf)) != -1) {
                out.write(buf, 0, size);
            }
            out.finish();
        } catch (IOException e) {
            logger.error("压缩文件[{}]出错", input);
            throw e;
        }
        if (delete) {
            if (new File(input).delete()) {
                logger.debug("删除文件[{}]成功", input);
            } else {
                logger.error("删除文件[{}]失败", input);
            }
        }
    }

    @PostConstruct
    void init() throws UnsupportedOptionsException {
        options = new LZMA2Options();
        options.setPreset(7);
    }

    private LZMA2Options options;
    private final static Logger logger = LoggerFactory.getLogger(ZipHelper.class);
}
