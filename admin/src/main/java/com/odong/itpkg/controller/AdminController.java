package com.odong.itpkg.controller;

import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.admin.*;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.model.SmtpProfile;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.util.DBHelper;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.email.EmailHelper;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:26
 */
@Controller
@RequestMapping(value = "/admin")
@SessionAttributes(SessionItem.KEY)
public class AdminController {

    @RequestMapping(value = "/company", method = RequestMethod.GET)
    String getCompanyList(Map<String, Object> map) {
        map.put("companyList", accountService.listCompany());
        return "admin/company";
    }


    @RequestMapping(value = "/company/state", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postCompany(@Valid CompanyStateForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);

        if (si.getSsCompanyId().equals(form.getCompany())) {
            ri.addData("管理员公司");
            ri.setOk(false);
        }

        if (ri.isOk()) {
            accountService.setCompanyState(form.getCompany(), form.getState());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "设置公司[" + form.getCompany() + "]状态[" + form.getState() + "]", Log.Type.INFO);
        }
        return ri;
    }

    @RequestMapping(value = "/smtp", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteSmtp() {
        SmtpProfile profile = jsonHelper.json2object(
                encryptHelper.decode(siteService.getString("site.smtp")),
                SmtpProfile.class);
        if (profile == null) {
            profile = new SmtpProfile();
        }

        Form fm = new Form("siteSmtp", "SMTP信息", "/admin/smtp");
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

    @RequestMapping(value = "/smtp", method = RequestMethod.POST)
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

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteBase() {
        Form fm = new Form("siteInfo", "站点信息", "/admin/info");
        fm.addField(new TextField<>("title", "标题", siteService.getString("site.title")));
        fm.addField(new TextField<>("domain", "域名", siteService.getString("site.domain")));
        fm.addField(new TextField<>("keywords", "关键字列表", siteService.getString("site.keywords")));
        TextAreaField taf = new TextAreaField("description", "说明信息", siteService.getString("site.description"));
        taf.setHtml(false);
        fm.addField(taf);
        fm.addField(new TextField<>("copyright", "版权信息", siteService.getString("site.copyright")));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postSiteInfo(@Valid SiteInfoForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            siteService.set("site.title", form.getTitle());
            siteService.set("site.domain", form.getDomain());
            siteService.set("site.keywords", form.getKeywords());
            siteService.set("site.description", form.getDescription());
            siteService.set("site.copyright", form.getCopyright());
            logService.add(si.getSsAccountId(), "设置站点基本信息", Log.Type.INFO);
            ri.setType(ResponseItem.Type.redirect);
            ri.addData("/personal/self");
        }
        return ri;

    }

    @RequestMapping(value = "/aboutMe", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteAboutMe() {
        Form fm = new Form("siteAboutMe", "关于我们", "/admin/aboutMe");
        TextAreaField taf = new TextAreaField("aboutMe", "内容", siteService.getString("site.aboutMe"));
        fm.addField(taf);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/aboutMe", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postSiteAboutMe(@Valid SiteAboutMeForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            siteService.set("site.aboutMe", form.getAboutMe());
            logService.add(si.getSsAccountId(), "设置关于我们信息", Log.Type.INFO);
        }
        return ri;

    }

    @RequestMapping(value = "/regProtocol", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteRegProtocol() {
        Form fm = new Form("siteRegProtocol", "注册协议", "/admin/regProtocol");
        TextAreaField taf = new TextAreaField("regProtocol", "内容", siteService.getString("site.regProtocol"));
        taf.setHtml(true);
        fm.addField(taf);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/regProtocol", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postRegProtocol(@Valid SiteRegProtocolForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            siteService.set("site.regProtocol", form.getRegProtocol());
            logService.add(si.getSsAccountId(), "设置用户注册协议", Log.Type.INFO);
        }
        return ri;

    }

    @RequestMapping(value = "/state", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteState() {
        Form fm = new Form("siteState", "网站状态", "/admin/state");
        RadioField<Boolean> login = new RadioField<>("allowLogin", "登陆", siteService.getBoolean("site.allowLogin"));
        login.addOption("允许", true);
        login.addOption("禁止", false);
        RadioField<Boolean> register = new RadioField<>("allowRegister", "注册", siteService.getBoolean("site.allowRegister"));
        register.addOption("允许", true);
        register.addOption("禁止", false);
        fm.addField(login);
        fm.addField(register);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/state", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postSiteState(@Valid SiteStateForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            siteService.set("site.allowLogin", form.isAllowLogin());
            siteService.set("site.allowRegister", form.isAllowRegister());
            logService.add(si.getSsAccountId(), "设置站点权限[登陆," + form.isAllowLogin() + "][注册," + form.isAllowRegister() + "]", Log.Type.INFO);
        }
        return ri;

    }

    @RequestMapping(value = "/compress", method = RequestMethod.GET)
    @ResponseBody
    Form getCompress() {
        Form fm = new Form("compress", "压缩数据", "/admin/compress");
        SelectField<Integer> daysKeep = new SelectField<>("days", "保留最近", 7);
        for (int i : new Integer[]{7, 30, 90, 180}) {
            daysKeep.addOption(i + "天", i);
        }
        fm.addField(daysKeep);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/compress", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postCompress(@Valid CompressForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (form.getDays() < 7) {
            ri.setOk(false);
            ri.addData("至少要保留最近一周的历史数据");
        }
        if (ri.isOk()) {
            dbHelper.compress(form.getDays());
            logService.add(si.getSsAccountId(), "压缩数据库，只保留最近[" + form.getDays() + "]天的数据", Log.Type.INFO);
        }
        return ri;

    }

    @Resource
    private DBHelper dbHelper;
    @Resource
    private FormHelper formHelper;
    @Resource
    private SiteService siteService;
    @Resource
    private EncryptHelper encryptHelper;
    @Resource
    private EmailHelper emailHelper;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private AccountService accountService;
    @Resource
    private LogService logService;

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setEmailHelper(EmailHelper emailHelper) {
        this.emailHelper = emailHelper;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }
}
