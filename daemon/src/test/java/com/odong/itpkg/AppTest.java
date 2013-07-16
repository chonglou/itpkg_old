package com.odong.itpkg;

import com.odong.itpkg.model.Rpc;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AppTest {
    @Test
    public void testWrite(){
        String name = "/tmp/aaa";
        Path file = Paths.get(name);
        try{
            Files.deleteIfExists(file);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        try (BufferedWriter writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"))) {
            for (String s : new String[]{"aaa", "bbb", "ccc"}) {
                writer.write(s, 0, s.length());
                writer.write('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
