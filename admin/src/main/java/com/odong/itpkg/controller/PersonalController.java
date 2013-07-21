package com.odong.itpkg.controller;

import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.form.personal.*;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.service.RbacService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.EmailHelper;
import com.odong.portal.util.FormHelper;
import com.odong.portal.util.TimeHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.PasswordField;
import com.odong.portal.web.form.TextAreaField;
import com.odong.portal.web.form.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    Form getInfo(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("info", "个人信息", "/personal/info");
        User u = accountService.getUser(si.getUserId());
        TextField<String> email = new TextField<>("email", "Email", u.getEmail());
        email.setReadonly(true);
        fm.addField(email);
        fm.addField(new TextField<>("username", "用户名", u.getUsername()));

        Contact c = jsonHelper.json2object(u.getContact(), Contact.class);
        if (c == null) {
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
    ResponseItem postInfo(@Valid ContactForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
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
            logService.add(si.getUserId(), "更新个人信息", Log.Type.INFO);
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
    ResponseItem postSetPwd(@Valid SetPwdForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (!form.getNewPwd().equals(form.getRePwd())) {
            ri.setOk(false);
            ri.addData("两次密码输入不一致");
        }
        if (ri.isOk()) {

            if (accountService.auth(si.getEmail(), form.getOldPwd()) == null) {
                ri.setOk(false);
                ri.addData("当前密码输入有误");
            } else {
                accountService.setUserPassword(si.getUserId(), form.getNewPwd());
                emailHelper.send(si.getEmail(), "您在[" + siteService.getString("site.domain") + "]上的密码变更记录",
                        "如果不是您的操作，请忽略该邮件。", true);
            }
        }
        return ri;

    }


    @RequestMapping(value = "/resetPwd/{code}", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem activateResetPwd(@PathVariable String code) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            Map<String, String> map = jsonHelper.json2map(encryptHelper.decode(code), String.class, String.class);
            String email = map.get("email");
            User user = accountService.getUser(email);
            if (user.getState() == User.State.ENABLE) {
                if (new Date().compareTo(timeHelper.plus(jsonHelper.json2object(map.get("created"), Date.class), 60 * 30)) <= 0) {
                    accountService.setUserPassword(user.getId(), map.get("password"));
                    ri.setOk(true);
                    logService.add(user.getId(), "重置密码", Log.Type.INFO);
                    emailHelper.send(email, "您在[" + siteService.getString("site.domain") + "]上的密码重置记录",
                            "如果不是您的操作，请忽略该邮件。", true);
                } else {
                    ri.addData("链接已失效");
                }
            } else {
                ri.addData("用户[" + user.getEmail() + "]状态不对");
            }
        } catch (Exception e) {
            logger.error("重置密码错误", e);
        }

        return ri;
    }

    @RequestMapping(value = "/resetPwd", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postResetPwd(@Valid ResetPwdForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (!form.getNewPwd().equals(form.getRePwd())) {
            ri.setOk(false);
            ri.addData("两次密码输入不一致");
        }
        if (ri.isOk()) {
            User u = accountService.getUser(form.getEmail());
            if (u != null && u.getState() == User.State.ENABLE) {

                Map<String, String> map = new HashMap<>();
                map.put("email", form.getEmail());
                map.put("password", form.getNewPwd());
                map.put("created", jsonHelper.object2json(new Date()));
                String domain = siteService.getString("site.domain");
                emailHelper.send(form.getEmail(), "您在[" + domain + "]上的密码重置",
                        "<a href='http://" + domain +
                                "/personal/resetPwd/" + jsonHelper.object2json(map) +
                                "' target='_blank'>请点击此链接重置密码</a>。<br/>如果不是您的操作，请忽略该邮件。",
                        true);
            } else {
                ri.addData("用户[" + form.getEmail() + "]状态不对");
            }
        }
        return ri;

    }

    @RequestMapping(value = "/resetPwd", method = RequestMethod.GET)
    @ResponseBody
    Form getResetPwd() {
        Form fm = new Form("resetPwd", "找回密码", "/personal/resetPwd");
        fm.addField(new TextField("email", "邮箱"));
        fm.addField(new PasswordField("password", "新密码"));
        fm.addField(new PasswordField("re_password", "再次输入"));
        fm.setCaptcha(true);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/active/{code}", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem activateRegister(@PathVariable String code) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try {
            Map<String, String> map = jsonHelper.json2map(code, String.class, String.class);
            String email = map.get("email");
            User u = accountService.getUser(email);
            if (u != null && u.getState() == User.State.SUBMIT) {
                accountService.setUserState(u.getId(), User.State.ENABLE);
                ri.setOk(true);
                logService.add(u.getId(), "账户激活", Log.Type.INFO);
            } else {
                ri.addData("账户[" + email + "]状态不对");
            }
        } catch (Exception e) {
            logger.error("激活账户失败", e);
        }
        return ri;
    }

    @RequestMapping(value = "/active", method = RequestMethod.GET)
    @ResponseBody
    Form getActive() {
        Form fm = new Form("active", "欢迎注册", "/personal/active");
        fm.addField(new TextField("email", "邮箱"));
        fm.setCaptcha(true);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/active", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postActive(@Valid ActiveForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (ri.isOk()) {
            sendActiveEmail(form.getEmail());
        }
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
        if (!form.getNewPwd().equals(form.getRePwd())) {
            ri.setOk(false);
            ri.addData("两次密码输入不一致");
        }
        if(!siteService.getBoolean("site.allowRegister")){
            ri.setOk(false);
            ri.addData("站点禁止注册");
        }
        if (ri.isOk()) {
            User u = accountService.getUser(form.getEmail());
            if (u == null) {
                String companyId = UUID.randomUUID().toString();
                accountService.addCompany(companyId, form.getCompany(), "");
                accountService.addUser(companyId, form.getEmail(), form.getUsername(), form.getNewPwd());
                sendActiveEmail(form.getEmail());
            } else {
                ri.setOk(false);
                ri.addData("邮箱[" + form.getEmail() + "]已存在");
            }
        }
        return ri;
    }

    private void sendActiveEmail(String email) {
        String domain = siteService.getString("site.domain");
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("created", jsonHelper.object2json(new Date()));
        emailHelper.send(email, "您在[" + domain + "]上的账户激活",
                "<a href='http://" + domain +
                        "/personal/active/" + jsonHelper.object2json(map) +
                        "' target='_blank'>请点击此链接激活账户</a>。<br/>如果不是您的操作，请忽略该邮件。",
                true);
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
    ResponseItem postLogin(@Valid LoginForm form, BindingResult result, HttpServletRequest request, HttpSession session) {
        ResponseItem ri = formHelper.check(result, request, true);
        if(!siteService.getBoolean("site.allowLogin")){
            ri.setOk(false);
            ri.addData("站点禁止登陆");
        }
        if (ri.isOk()) {
            User u = accountService.auth(form.getEmail(), form.getPassword());
            if (u == null) {
                ri.setOk(false);
                ri.addData("账户密码不匹配");
            } else {
                Company c = accountService.getCompany(u.getCompany());
                if (c.getState() == Company.State.ENABLE) {
                    switch (u.getState()) {
                        case ENABLE:
                            SessionItem si = new SessionItem();
                            si.setUsername(u.getUsername());
                            si.setEmail(u.getEmail());
                            si.setUserId(u.getId());
                            si.setCompanyId(u.getCompany());
                            si.setAdmin(rbacService.authAdmin(u.getId()));
                            si.setCreated(new Date());

                            ri.setType(ResponseItem.Type.redirect);
                            ri.addData("/personal/self");
                            session.setAttribute(SessionItem.KEY, si);
                            logService.add(u.getId(), "用户登陆", Log.Type.INFO);
                            break;
                        case DISABLE:
                            ri.setOk(false);
                            ri.addData("账户[" + form.getEmail() + "]被禁用");
                            break;
                        case SUBMIT:
                            ri.setOk(false);
                            ri.addData("账户[" + form.getEmail() + "]未激活");
                            break;
                        default:
                            ri.setOk(false);
                            ri.addData("未知错误");
                            break;
                    }
                } else {
                    ri.setOk(false);
                    ri.addData("公司[" + c.getName() + "]被禁用");
                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem getLogout(HttpSession session) {
        //ResponseItem logout(SessionStatus status) {
        //status.setComplete();
        SessionItem si = (SessionItem) session.getAttribute(SessionItem.KEY);
        logService.add(si.getUserId(), "注销登陆", Log.Type.INFO);
        session.invalidate();
        ResponseItem ri = new ResponseItem(ResponseItem.Type.redirect);
        ri.addData("/");
        ri.setOk(true);
        return ri;
    }


    @Resource
    private TimeHelper timeHelper;
    @Resource
    private SiteService siteService;
    @Resource
    private EmailHelper emailHelper;
    @Resource
    private FormHelper formHelper;
    @Resource
    private JsonHelper jsonHelper;
    @Resource
    private EncryptHelper encryptHelper;
    @Resource
    private AccountService accountService;
    @Resource
    private RbacService rbacService;
    @Resource
    private LogService logService;
    private final static Logger logger = LoggerFactory.getLogger(PersonalController.class);

    public void setRbacService(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setTimeHelper(TimeHelper timeHelper) {
        this.timeHelper = timeHelper;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setEmailHelper(EmailHelper emailHelper) {
        this.emailHelper = emailHelper;
    }

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
