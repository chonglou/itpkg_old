package com.odong.portal.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.odong.itpkg.model.KaptchaProfile;
import com.odong.itpkg.model.ReCaptchaProfile;
import com.odong.portal.service.SiteService;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
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
    ReCaptcha getRecaptcha(){
        ReCaptchaImpl captcha = new ReCaptchaImpl();
        captcha.setPrivateKey(reCaptcha.getPrivateKey());
        captcha.setPublicKey(reCaptcha.getPublicKey());
        captcha.setIncludeNoscript(reCaptcha.isIncludeNoScript());
        return captcha;
    }
    @Bean
    Producer getKaptchaProducer() {
        Properties props = new Properties();
        props.setProperty("kaptcha.image.width", Integer.toString(kaptcha.getWidth()));
        props.setProperty("kaptcha.image.height", Integer.toString(kaptcha.getHeight()));
        props.setProperty("kaptcha.textproducer.char.string", kaptcha.getChars());
        props.setProperty("kaptcha.textproducer.char.length", Integer.toString(kaptcha.getLength()));
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(new Config(props));
        return kaptcha;
    }


    @PostConstruct
    void  init(){
        kaptcha = siteService.getObject("site.kaptcha", KaptchaProfile.class);
        reCaptcha = siteService.getObject("site.reCaptcha", ReCaptchaProfile.class);
    }

    @Resource
    private SiteService siteService;
    private KaptchaProfile kaptcha;
    private ReCaptchaProfile reCaptcha;

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

}
