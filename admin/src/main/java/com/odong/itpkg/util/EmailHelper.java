package com.odong.itpkg.util;

import com.odong.itpkg.model.SmtpProfile;
import com.odong.itpkg.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午5:08
 */
@Component
public class EmailHelper {
    public void send(String to, String title, String body, boolean html) {
        send(to, title, body, html, new HashMap<String, String>());
    }

    public void send(String to, String title, String body, boolean html, Map<String, String> attachs) {
        if (sender == null) {
            throw new IllegalArgumentException("SMTP信息未配置");
        }
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(title);
            helper.setTo(to);
            if (profile.getFrom() != null) {
                helper.setFrom(profile.getFrom());
            }
            if (profile.getBcc() != null) {
                helper.setBcc(profile.getBcc());
            }
            helper.setText(body, html);
            for (String file : attachs.keySet()) {
                helper.addInline(attachs.get(file), new FileSystemResource(file));
            }
            sender.send(message);
        } catch (MessagingException e) {
            logger.error("发送邮件失败", e);
        }

    }


    public void setup(SmtpProfile profile) {
        siteService.set("site.smtp", encryptHelper.encode(profile));
        this.profile = profile;
    }

    @PostConstruct
    public void reload() {
        sender = null;
        profile = encryptHelper.decode(siteService.getString("site.smtp"), SmtpProfile.class);
        if (profile != null) {
            try {
                sender = new JavaMailSenderImpl();
                sender.setHost(profile.getHost());
                sender.setUsername(profile.getUsername());
                sender.setPassword(profile.getPassword());
                sender.setDefaultEncoding("UTF-8");
                Properties props = new Properties();
                props.put("mail.smtp.auth", true);
                props.put("mail.smtp.timeout", 25000);
                sender.setJavaMailProperties(props);
            } catch (Exception e) {
                logger.error("邮件配置有误", e);
            }
        }
    }

    private JavaMailSenderImpl sender;

    @Resource
    private SiteService siteService;
    @Resource
    private EncryptHelper encryptHelper;
    private SmtpProfile profile;
    private final static Logger logger = LoggerFactory.getLogger(EmailHelper.class);


    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
