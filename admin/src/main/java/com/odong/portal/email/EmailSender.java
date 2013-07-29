package com.odong.portal.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-27
 * Time: 下午1:27
 */
public class EmailSender implements Runnable {

    public EmailSender(String host,
                       int port,
                       String username,
                       String password,
                       boolean ssl,
                       String bcc,
                       String to, String title, String body, boolean html, Map<String, String> attachs) {

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setProtocol(ssl ? "smtps" : "smtp");
        sender.setDefaultEncoding("UTF-8");
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.timeout", 25000);
        props.put("mail.smtp.quitwait", false);
        if (ssl) {
            props.put("mail.smtps.auth", true);
            props.put("mail.smtp.starttls.enable", ssl);
        }
        sender.setJavaMailProperties(props);

        this.sender = sender;
        this.username = username;
        this.bcc = bcc;
        this.to = to;
        this.title = title;
        this.body = body;
        this.html = html;
        this.attachs = attachs;
    }

    @Override
    public void run() {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(title);
            helper.setTo(to);
            helper.setFrom(username);
            if (bcc != null) {
                helper.setBcc(bcc);
            }
            helper.setText(body, html);
            for (String file : attachs.keySet()) {
                helper.addInline(attachs.get(file), new FileSystemResource(file));
            }
            sender.send(message);
            logger.debug("发送邮件成功[{},{}]", to, title);
        } catch (Exception e) {
            logger.error("发送邮件失败", e);
        }
    }

    private JavaMailSender sender;
    private String username;
    private String bcc;
    private String to;
    private String title;
    private String body;
    private boolean html;
    private Map<String, String> attachs;
    private final static Logger logger = LoggerFactory.getLogger(EmailSender.class);

}
