package com.odong.itpkg.controller.admin;

import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.admin.SiteSmtpForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.model.SmtpProfile;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.email.EmailHelper;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.RadioField;
import com.odong.portal.web.form.TextField;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午12:54
 */
@Controller("c.admin.smtp")
@RequestMapping(value = "/admin/smtp")
@SessionAttributes(SessionItem.KEY)
public class SmtpController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteSmtp() {
        SmtpProfile profile = jsonHelper.json2object(
                encryptHelper.decode(siteService.getString("site.smtp")),
                SmtpProfile.class);
        if (profile == null) {
            profile = new SmtpProfile();
        }

        Form fm = new Form("siteSmtp", "SMTP信息", "/admin/smtp/");
        fm.addField(new TextField<>("host", "主机", profile.getHost()));
        fm.addField(new TextField<>("port", "端口", profile.getPort()));
        fm.addField(new TextField<>("username", "用户名", profile.getUsername()));
        fm.addField(new TextField<>("password", "密码"));
        RadioField<Boolean> ssl = new RadioField<>("ssl", "启用SSL", profile.isSsl());
        ssl.addOption("是", true);
        ssl.addOption("否", false);
        fm.addField(ssl);
        TextField<String> bcc = new TextField<>("bcc", "密送", profile.getBcc());
        bcc.setRequired(false);
        fm.addField(bcc);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postSiteSmtp(@Valid SiteSmtpForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            SmtpProfile profile = new SmtpProfile(form.getHost(), form.getUsername(), form.getPassword(), form.getBcc());
            profile.setPort(form.getPort());
            profile.setSsl(form.isSsl());
            siteService.set("site.smtp", encryptHelper.encode(jsonHelper.object2json(profile)));
            logService.add(si.getSsAccountId(), "设置SMTP信息", Log.Type.INFO);
            emailHelper.reload();

        }
        return ri;

    }

    @Resource
    private SiteService siteService;
    @Resource
    private EncryptHelper encryptHelper;
    @Resource
    private EmailHelper emailHelper;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private LogService logService;
    @Resource
    private FormHelper formHelper;

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setEmailHelper(EmailHelper emailHelper) {
        this.emailHelper = emailHelper;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }
}
