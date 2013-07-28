package com.odong.portal.util;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.odong.itpkg.model.KaptchaProfile;
import com.odong.itpkg.model.ReCaptchaProfile;
import com.odong.portal.service.SiteService;
import httl.spi.resolvers.GlobalResolver;
import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午11:23
 */
@Component
public class CaptchaHelper {


    @PostConstruct
    public void reload() {
        KaptchaProfile kp = siteService.getObject("site.kaptcha", KaptchaProfile.class);

        Properties props = new Properties();
        props.setProperty("kaptcha.image.width", Integer.toString(kp.getWidth()));
        props.setProperty("kaptcha.image.height", Integer.toString(kp.getHeight()));
        props.setProperty("kaptcha.textproducer.char.string", kp.getChars());
        props.setProperty("kaptcha.textproducer.char.length", Integer.toString(kp.getLength()));
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(new Config(props));
        this.kaptcha = kaptcha;


        ReCaptchaProfile rp = siteService.getObject("site.reCaptcha", ReCaptchaProfile.class);
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey(rp.getPrivateKey());
        reCaptcha.setPublicKey(rp.getPublicKey());
        reCaptcha.setIncludeNoscript(rp.isIncludeNoScript());
        this.reCaptcha = reCaptcha;

        String captcha = siteService.getString("site.captcha");
        GlobalResolver.put("gl_captcha", captcha);
        if ("reCaptcha".equals(captcha)) {
            GlobalResolver.put("gl_reCaptcha_key", siteService.getObject("site.reCaptcha", ReCaptchaProfile.class).getPublicKey());
        }

        logger.info("重新加载验证码配置");
    }


    private Producer kaptcha;
    private ReCaptcha reCaptcha;
    @Resource
    private SiteService siteService;
    private final static Logger logger = LoggerFactory.getLogger(CaptchaHelper.class);

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public Producer getKaptcha() {
        return kaptcha;
    }

    public ReCaptcha getReCaptcha() {
        return reCaptcha;
    }
}
