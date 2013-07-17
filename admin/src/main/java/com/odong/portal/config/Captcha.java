package com.odong.portal.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午11:23
 */
@Configuration("config.captcha")
public class Captcha {
    @Bean
    Producer getKaptchaProducer() {
        Properties props = new Properties();
        props.setProperty("kaptcha.image.width", Integer.toString(imgWidth));
        props.setProperty("kaptcha.image.height", Integer.toString(imgHeight));
        props.setProperty("kaptcha.textproducer.char.string", charString);
        props.setProperty("kaptcha.textproducer.char.length", Integer.toString(charLength));
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(new Config(props));
        return kaptcha;
    }

    @Value("${kaptcha.image_width}")
    private int imgWidth;
    @Value("${kaptcha.image_height}")
    private int imgHeight;
    @Value("${kaptcha.char_string}")
    private String charString;
    @Value("${kaptcha.char_length}")
    private int charLength;

    public void setImgWidth(int imgWidth) {
        this.imgWidth = imgWidth;
    }

    public void setImgHeight(int imgHeight) {
        this.imgHeight = imgHeight;
    }

    public void setCharString(String charString) {
        this.charString = charString;
    }

    public void setCharLength(int charLength) {
        this.charLength = charLength;
    }
}
