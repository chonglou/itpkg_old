package com.odong.itpkg.controller;

import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.form.admin.*;
import com.odong.itpkg.model.SmtpProfile;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.RbacService;
import com.odong.itpkg.util.DBHelper;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import com.odong.itpkg.util.SiteHelper;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.EmailHelper;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
public class AdminController {
    @RequestMapping(value = "/site/company/({companyId},{state})", method = RequestMethod.POST)
    ResponseItem postCompany(@PathVariable String companyId, @PathVariable Company.State state) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        if("admin".equals(companyId)){
            ri.addData("管理员公司，不能被删除");
        }
        else {
            accountService.setCompanyState(companyId, state);
            ri.setOk(true);
        }
        return ri;
    }

    @RequestMapping(value = "/site/company", method = RequestMethod.GET)
    String getCompanyList(Map<String, Object> map) {
        map.put("users", accountService.listUser());
        map.put("companies", accountService.listCompany());
        return "admin/companyList";
    }

    @RequestMapping(value = "/site/smtp", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteSmtp() {
        SmtpProfile profile = jsonHelper.json2object(
                encryptHelper.decode(siteService.getString("site.smtp")),
                SmtpProfile.class);
        if (profile == null) {
            profile = new SmtpProfile();
        }
        Form fm = new Form("siteSmtp", "SMTP信息", "/admin/site/smtp");
        fm.addField(new TextField<>("host", "主机", profile.getHost()));
        fm.addField(new TextField<>("port", "端口", profile.getPort()));
        fm.addField(new TextField<>("username", "用户名", profile.getUsername()));
        fm.addField(new TextField<>("password", "密码", profile.getPassword()));
        fm.addField(new TextField<>("from", "发信人", profile.getFrom()));
        fm.addField(new TextField<>("bcc", "密送", profile.getBcc()));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/site/smtp", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postSiteSmtp(@Valid SiteSmtpForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            SmtpProfile profile = new SmtpProfile(form.getHost(), form.getUsername(), form.getPassword(), form.getBcc());
            profile.setPort(form.getPort());
            profile.setFrom(form.getFrom());
            siteService.set("site.smtp", encryptHelper.encode(jsonHelper.object2json(profile)));

            emailHelper.reload();
        }
        return ri;

    }

    @RequestMapping(value = "/site/info", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteBase() {
        Form fm = new Form("siteInfo", "站点信息", "/admin/site/info");
        fm.addField(new TextField<>("title", "标题", siteService.getString("site.title")));
        fm.addField(new TextField<>("domain", "域名", siteService.getString("site.domain")));
        fm.addField(new TextField<>("keywords", "关键字列表", siteService.getString("site.keywords")));
        fm.addField(new TextAreaField("description", "说明信息", siteService.getString("site.description")));
        fm.addField(new TextField<>("copyright", "版权信息", siteService.getString("site.copyright")));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/site/info", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postSiteInfo(@Valid SiteInfoForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            siteService.set("site.title", form.getTitle());
            siteService.set("site.domain", form.getDomain());
            siteService.set("site.keywords", form.getKeywords());
            siteService.set("site.description", form.getDescription());
            siteService.set("site.copyright", form.getCopyright());
        }
        return ri;

    }

    @RequestMapping(value = "/site/aboutMe", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteAboutMe() {
        Form fm = new Form("siteAboutMe", "关于我们", "/admin/site/aboutMe");
        TextAreaField taf = new TextAreaField("aboutMe", "内容", siteService.getString("site.aboutMe"));
        taf.setHtml(true);
        fm.addField(taf);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/site/aboutMe", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postSiteAboutMe(@Valid SiteAboutMeForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            siteService.set("site.aboutMe", form.getAboutMe());
        }
        return ri;

    }

    @RequestMapping(value = "/site/regProtocol", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteRegProtocol() {
        Form fm = new Form("siteRegProtocol", "注册协议", "/admin/site/regProtocol");
        TextAreaField taf = new TextAreaField("regProtocol", "内容", siteService.getString("site.regProtocol"));
        taf.setHtml(true);
        fm.addField(taf);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/site/regProtocol", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postRegProtocol(@Valid SiteRegProtocolForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            siteService.set("site.regProtocol", form.getRegProtocol());
        }
        return ri;

    }

    @RequestMapping(value = "/site/state", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteState() {
        Form fm = new Form("siteState", "网站状态", "/admin/site/state");
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

    @RequestMapping(value = "/site/state", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postSiteState(@Valid SiteStateForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            siteService.set("site.allowLogin", form.isAllowLogin());
            siteService.set("site.allowRegister", form.isAllowRegister());
        }
        return ri;

    }

    @RequestMapping(value = "/compress", method = RequestMethod.GET)
    @ResponseBody
    Form getCompress() {
        Form fm = new Form("compress", "压缩数据", "/admin/compress");
        SelectField<Integer> daysKeep = new SelectField<>("days", "保留最近", 7);
        for (int i : new Integer[]{1, 3, 7, 30, 90, 180}) {
            daysKeep.addOption(i + "天", i);
        }
        fm.addField(daysKeep);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/compress", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postCompress(@Valid CompressForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            dbHelper.compress(form.getDays());
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
