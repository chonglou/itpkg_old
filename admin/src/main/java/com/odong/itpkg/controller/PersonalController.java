package com.odong.itpkg.controller;

import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.form.personal.*;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.PasswordField;
import com.odong.portal.web.form.TextAreaField;
import com.odong.portal.web.form.TextField;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 上午10:49
 */
@Controller
@RequestMapping(value = "/personal")
 @SessionAttributes(SessionItem.KEY)
public class PersonalController {

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    Form getInfo(@ModelAttribute(SessionItem.KEY) SessionItem si ) {
        Form fm = new Form("info", "个人信息", "/personal/info");
        User u= accountService.getUser(si.getUserId());
        TextField<String> email = new TextField<>("email", "Email", u.getEmail());
        email.setReadonly(true);
        fm.addField(email);
        fm.addField(new TextField<>("username", "用户名",u.getUsername()));

        Contact c = jsonHelper.json2object(u.getContact(), Contact.class);
        if(c == null){
            c = new Contact();
        }
        fm.addField(new TextField<>("qq", "QQ号", c.getQq()));
        fm.addField(new TextField<>("tel", "电话", c.getTel()));
        fm.addField(new TextField<>("fax", "传真", u));
        fm.addField(new TextAreaField("address", "地址", c.getAddress()));
        fm.addField(new TextField<>("weixin", "微信", c.getWeixin()));
        fm.addField(new TextField<>("web", "个人站点", c.getWeb()));
        TextAreaField taf = new TextAreaField("details", "详细信息", c.getDetails());
        taf.setHtml(true);
        fm.addField(taf);
        fm.setOk(true);
        return fm;
    }
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postInfo(@Valid ContactForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si ) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            Contact c = new Contact();
            c.setAddress(form.getAddress());
            c.setFax(form.getFax());
            c.setTel(form.getTel());
            c.setQq(form.getQq());
            c.setWeb(form.getWeb());
            c.setWeixin(form.getWeixin());
            c.setDetails(form.getDetails());
            accountService.setUserInfo(si.getUserId(), form.getUsername(), c);
            si.setUsername(form.getUsername());
            ri.setType(ResponseItem.Type.redirect);
            ri.addData("/personal");
        }
        return ri;

    }

    @RequestMapping(value = "/setPwd", method = RequestMethod.GET)
    @ResponseBody
    Form getSetPwd() {
        Form fm = new Form("setPwd", "设置密码", "/personal/setPwd");
        fm.addField(new PasswordField("oldPwd", "当前密码"));
        fm.addField(new PasswordField("newPwd", "新密码"));
        fm.addField(new PasswordField("rePwd", "再输一遍"));
        fm.setOk(true);
        return fm;
    }
    @RequestMapping(value = "/setPwd", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postSetPwd(@Valid SetPwdForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            //TODO
        }
        return ri;

    }


    @RequestMapping(value = "/resetPwd/{code}", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem activateResetPwd(@PathVariable String code) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.redirect);
        //TODO
        return ri;
    }
    @RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postResetPwd(@Valid ResetPwdForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            //TODO
        }
        return ri;

    }
    @RequestMapping(value = "/resetPwd", method = RequestMethod.GET)
    @ResponseBody
    Form getResetPwd(){
        Form fm = new Form("resetPwd", "找回密码", "/personal/resetPwd");
        fm.addField(new TextField("email", "邮箱"));
        fm.addField(new PasswordField("password", "新密码"));
        fm.addField(new PasswordField("re_password", "再次输入"));
        fm.setCaptcha(true);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/register/{code}", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem activateRegister(@PathVariable String code) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.redirect);
        //TODO
        return ri;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    @ResponseBody
    Form getRegister() {
        Form fm = new Form("register", "欢迎注册", "/personal/register");
        fm.addField(new TextField("company", "公司名称"));
        fm.addField(new TextField("email", "邮箱"));
        fm.addField(new PasswordField("password", "登陆密码"));
        fm.addField(new PasswordField("re_password", "再次输入"));
        fm.setCaptcha(true);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postRegister(@Valid RegisterForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            //TODO
        }
        return ri;
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    Form getLogin() {
        Form fm = new Form("login", "欢迎登录", "/personal/login");
        fm.addField(new TextField("email", "邮箱"));
        fm.addField(new PasswordField("password", "密码"));
        fm.setCaptcha(true);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postLogin(@Valid LoginForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            //TODO
        }
        return ri;
    }

      @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem logout(SessionStatus status) {

        //session.invalidate();
        status.setComplete();
        ResponseItem ri = new ResponseItem(ResponseItem.Type.redirect);
        ri.addData("/");
        ri.setOk(true);
        return ri;
    }


    @Resource
    private FormHelper formHelper;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private AccountService accountService;

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }
}
