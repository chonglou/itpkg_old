package com.odong.itpkg;

import com.odong.itpkg.model.Rpc;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class AppTest {
    @Test
    public void testWrite(){
        Properties props = System.getProperties();
        for(Object t : props.keySet()){
            log(t+"="+props.getProperty(t.toString()));
        }
    }
    private void log(String... ss){
        for(String s : ss){
            System.out.println(s);
        }
    }
}
