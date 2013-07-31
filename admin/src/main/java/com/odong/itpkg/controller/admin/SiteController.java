package com.odong.itpkg.controller.admin;

import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.admin.SiteAboutMeForm;
import com.odong.itpkg.form.admin.SiteInfoForm;
import com.odong.itpkg.form.admin.SiteRegProtocolForm;
import com.odong.itpkg.form.admin.SiteStateForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.LogService;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.RadioField;
import com.odong.portal.web.form.TextAreaField;
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
 * Time: 下午12:51
 */

@Controller("c.admin.site")
@RequestMapping(value = "/admin/site")
@SessionAttributes(SessionItem.KEY)
public class SiteController {


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    Form getSiteBase() {
        Form fm = new Form("siteInfo", "站点信息", "/admin/site/info");
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
        Form fm = new Form("siteAboutMe", "关于我们", "/admin/site/aboutMe");
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
        Form fm = new Form("siteRegProtocol", "注册协议", "/admin/site/regProtocol");
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


    @Resource
    private SiteService siteService;
    @Resource
    private LogService logService;
    @Resource
    private FormHelper formHelper;

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }
}
