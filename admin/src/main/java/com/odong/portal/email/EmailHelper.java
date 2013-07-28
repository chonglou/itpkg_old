package com.odong.portal.email;

import com.odong.itpkg.model.SmtpProfile;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午5:08
 */
@Component("emailHelper")
public class EmailHelper {
    public void send(String to, String title, String body, boolean html) {
        send(to, title, body, html, new HashMap<String, String>());
    }

    public void send(String to, String title, String body, boolean html, Map<String, String> attachs) {
        if (profile == null) {
            logger.error("SMTP信息未配置");
            return;
        }
        taskExecutor.execute(new EmailSender(
                profile.getHost(), profile.getPort(), profile.getUsername(), profile.getPassword(), profile.isSsl(), profile.getBcc(),
                to, title, body, html, attachs));


    }


    public void setup(SmtpProfile profile) {
        siteService.set("site.smtp", encryptHelper.encode(jsonHelper.object2json(profile)));
        this.profile = profile;
    }

    @PostConstruct
    public void reload() {
        profile = jsonHelper.json2object(encryptHelper.decode(siteService.getString("site.smtp")), SmtpProfile.class);
    }


    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private SiteService siteService;
    @Resource
    private EncryptHelper encryptHelper;
    @Resource
    private TaskExecutor taskExecutor;
    private SmtpProfile profile;

    private final static Logger logger = LoggerFactory.getLogger(EmailHelper.class);

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
