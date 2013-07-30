package com.odong.itpkg.controller.personal;

import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.email.EmailHelper;
import com.odong.portal.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午1:08
 */
public abstract class EmailController {

    enum Type {
        REGISTER, RESET_PWD
    }


    protected void sendValidEmail(String email, Type type, Map<String, String> args) {
        String domain = siteService.getString("site.domain");
        args.put("email", email);
        args.put("type", type.toString());
        args.put("created", jsonHelper.object2json(new Date()));

        String title = "";
        String content = "";
        switch (type) {
            case REGISTER:
                title = "创建了账户";
                content = "激活账户";
                break;
            case RESET_PWD:
                title = "重置了密码";
                content = "重置密码";
                break;
        }


        try {
            emailHelper.send(
                    email,
                    "您在[" + domain + "(" + siteService.getString("site.title") + ")]上" + title + "，请激活",
                    "<a href='http://" + domain + "/personal/valid?code=" +
                            URLEncoder.encode(encryptHelper.encode(jsonHelper.object2json(args)), "UTF-8")

                            + "' target='_blank'>请点击此链接以" + content + "(" + linkValid + "分钟内有效)</a>。" +
                            "<br/>如果不是您的操作，请忽略该邮件。",
                    true);
        } catch (UnsupportedEncodingException e) {
            logger.error("不支持编码", e);
        }
    }

    @Resource
    protected EmailHelper emailHelper;
    @Resource
    protected EncryptHelper encryptHelper;
    @Resource
    protected SiteService siteService;
    @Resource
    protected JsonHelper jsonHelper;
    @Value("${link.valid}")
    protected int linkValid;
    private final static Logger logger = LoggerFactory.getLogger(EmailController.class);

    public void setEmailHelper(EmailHelper emailHelper) {
        this.emailHelper = emailHelper;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setLinkValid(int linkValid) {
        this.linkValid = linkValid;
    }
}
