package com.odong.itpkg.controller;

import com.odong.itpkg.entity.uc.Account;
import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.personal.*;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.service.RbacService;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.email.EmailHelper;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.util.TimeHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.PasswordField;
import com.odong.portal.web.form.TextAreaField;
import com.odong.portal.web.form.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
@Controller("c.personal")
@RequestMapping(value = "/personal")
@SessionAttributes(SessionItem.KEY)
public class PersonalController {


    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    Form getInfo(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("info", "个人信息", "/personal/info");
        Account u = accountService.getAccount(si.getAccountId());
        TextField<String> email = new TextField<>("email", "Email", u.getEmail());
        email.setReadonly(true);
        fm.addField(email);
        fm.addField(new TextField<>("username", "用户名", u.getUsername()));

        Contact c = jsonHelper.json2object(u.getContact(), Contact.class);
        if (c == null) {
            c = new Contact();
        }
        String[] ss = new String[]{
                "qq", "QQ号", c.getQq(),
                "tel", "电话", c.getTel(),
                "fax", "传真", c.getFax(),
                "address", "地址", c.getAddress(),
                "weixin", "微信", c.getWeixin(),
                "web", "个人站点", c.getWeb()
        };
        for (int i = 0; i < ss.length; i += 3) {
            TextField<String> tf = new TextField<>(ss[i], ss[i + 1], ss[i + 2]);
            tf.setRequired(false);
            fm.addField(tf);
        }
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
            accountService.setAccountInfo(si.getAccountId(), form.getUsername(), c);
            si.setUsername(form.getUsername());
            ri.setType(ResponseItem.Type.redirect);
            ri.addData("/personal/self");
            logService.add(si.getAccountId(), "更新个人信息", Log.Type.INFO);
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
                accountService.setAccountPassword(si.getAccountId(), form.getNewPwd());
                emailHelper.send(si.getEmail(), "您在[" + siteService.getString("site.domain") + "]上的密码变更记录",
                        "如果不是您的操作，请忽略该邮件。", true);
            }
        }
        return ri;

    }


    @RequestMapping(value = "/log", method = RequestMethod.GET)
    String getLog(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        map.put("logList", logService.list(si.getAccountId(), 100));
        return "personal/log";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem getLogout(HttpSession session) {
        SessionItem si = (SessionItem) session.getAttribute(SessionItem.KEY);
        logService.add(si.getAccountId(), "注销登陆", Log.Type.INFO);
        session.invalidate();
        ResponseItem ri = new ResponseItem(ResponseItem.Type.redirect);
        ri.addData("/");
        ri.setOk(true);
        return ri;
    }

    private void activeAccount(long userId, String email){
        accountService.setAccountState(userId, Account.State.ENABLE);
        logService.add(userId, "账户激活", Log.Type.INFO);
        emailHelper.send(
                email,
                "您在[" + siteService.getString("site.title") + "]上的激活了账户",
                "欢迎使用",
                true);
    }

    @RequestMapping(value = "/valid", method = RequestMethod.GET)
    String getValidCode(HttpServletRequest request, Map<String, Object> map) {

        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Map<String, String> mapA = jsonHelper.json2map(encryptHelper.decode(request.getParameter("code")), String.class, String.class);
        String email = mapA.get("email");
        String type = mapA.get("type");
        String created = mapA.get("created");

        if (email == null || type == null || created == null) {
            ri.addData("链接信息不全");
        } else if (timeHelper.plus(new Date(Long.parseLong(created)), 60 * linkValid).compareTo(new Date()) < 0
                ) {
            ri.addData("链接失效，请重新申请。");
        } else {
            Account u = accountService.getAccount(email);
            switch (Type.valueOf(type)) {
                case REGISTER:
                    if (u != null && u.getState() == Account.State.SUBMIT) {
                        Company c = accountService.getCompany(u.getCompany());

                        switch (c.getState()) {
                            case SUBMIT:
                                accountService.setCompanyState(u.getCompany(), Company.State.ENABLE);
                                logService.add(u.getId(), "公司激活", Log.Type.INFO);
                                activeAccount(u.getId(), u.getEmail());
                                rbacService.bindCompany(u.getId(), u.getCompany(), RbacService.OperationType.MANAGE, true);
                                logService.add(u.getId(), "授予自己公司管理员权限", Log.Type.INFO);
                                ri.setOk(true);
                                ri.addData("您成功激活了公司和用户");
                                break;
                            case ENABLE:
                                activeAccount(u.getId(), u.getEmail());
                                rbacService.bindCompany(u.getId(), u.getCompany(), RbacService.OperationType.USE, true);
                                logService.add(u.getId(), "授予公司用户权限", Log.Type.INFO);
                                ri.setOk(true);
                                ri.addData("您成功激活了账户");
                                break;
                            default:
                                ri.addData("公司[" + c.getName() + "]状态不对");
                                break;
                        }


                    } else {
                        ri.addData("账户[" + email + "]状态不对");
                    }
                    break;
                case RESET_PWD:
                    if (u.getState() == Account.State.ENABLE) {
                        if (new Date().compareTo(timeHelper.plus(jsonHelper.json2object(mapA.get("created"), Date.class), 60 * 30)) <= 0) {
                            accountService.setAccountPassword(u.getId(), mapA.get("password"));
                            logService.add(u.getId(), "重置密码", Log.Type.INFO);
                            emailHelper.send(email, "您在[" + siteService.getString("site.domain") + "]上的成功重置了密码",
                                    "如果不是您的操作，请忽略该邮件。", true);
                            ri.setOk(true);
                            ri.addData("您成功重置了密码");
                        } else {
                            ri.addData("链接已失效");
                        }
                    } else {
                        ri.addData("用户[" + u.getEmail() + "]状态不对");
                    }
                    break;
                default:
                    ri.addData("未知的操作");
                    break;
            }
        }
        map.put("item", ri);
        return "message";
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
            Account u = accountService.getAccount(form.getEmail());
            if (u != null && u.getState() == Account.State.ENABLE) {
                Map<String, String> map = new HashMap<>();
                map.put("password", form.getNewPwd());
                sendValidEmail(form.getEmail(), Type.RESET_PWD, map);
                ri.setOk(true);

            } else {
                ri.addData("用户[" + form.getEmail() + "]状态不对");
                ri.setOk(false);
            }
        }
        return ri;

    }

    @RequestMapping(value = "/resetPwd", method = RequestMethod.GET)
    @ResponseBody
    Form getResetPwd() {
        Form fm = new Form("resetPwd", "找回密码", "/personal/resetPwd");
        fm.addField(new TextField("email", "邮箱"));
        fm.addField(new PasswordField("newPwd", "新密码"));
        fm.addField(new PasswordField("rePwd", "再次输入"));
        fm.setCaptcha(true);
        fm.setOk(true);
        return fm;
    }


    @RequestMapping(value = "/active", method = RequestMethod.GET)
    @ResponseBody
    Form getActive() {
        Form fm = new Form("active", "激活账户", "/personal/active");
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
            Account a = accountService.getAccount(form.getEmail());
            if (a != null && a.getState() == Account.State.SUBMIT) {
                sendValidEmail(form.getEmail(), Type.REGISTER, new HashMap<String, String>());
            } else {
                ri.setOk(false);
                ri.addData("邮箱[" + form.getEmail() + "]状态不对");
            }
        }
        return ri;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    @ResponseBody
    Form getRegister() {
        Form fm = new Form("register", "注册账户", "/personal/register");
        fm.addField(new TextField("company", "公司名称"));
        fm.addField(new TextField("email", "邮箱"));
        fm.addField(new TextField("username", "用户名"));
        fm.addField(new PasswordField("newPwd", "登陆密码"));
        fm.addField(new PasswordField("rePwd", "再次输入"));
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
        if (!siteService.getBoolean("site.allowRegister")) {
            ri.setOk(false);
            ri.addData("站点禁止注册新账户");
        }
        if (ri.isOk()) {
            Account u = accountService.getAccount(form.getEmail());
            if (u == null) {
                String companyId = UUID.randomUUID().toString();
                accountService.addCompany(companyId, form.getCompany(), "暂无");
                accountService.addAccount(companyId, form.getEmail(), form.getUsername(), form.getNewPwd());
                sendValidEmail(form.getEmail(), Type.REGISTER, new HashMap<String, String>());
            } else {
                ri.setOk(false);
                ri.addData("邮箱[" + form.getEmail() + "]已存在");
            }
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
    ResponseItem postLogin(@Valid LoginForm form, BindingResult result, HttpServletRequest request, HttpSession session) {
        ResponseItem ri = formHelper.check(result, request, true);
        if (!siteService.getBoolean("site.allowLogin")) {
            ri.setOk(false);
            ri.addData("站点禁止登陆");
        }
        if (ri.isOk()) {
            Account u = accountService.auth(form.getEmail(), form.getPassword());
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
                            si.setAccountId(u.getId());
                            si.setCompanyId(u.getCompany());
                            si.setAdmin(rbacService.authAdmin(u.getId()));
                            si.setCompanyManager(rbacService.authCompany(u.getId(), u.getCompany(), RbacService.OperationType.MANAGE));
                            si.setCreated(new Date());

                            ri.setType(ResponseItem.Type.redirect);
                            ri.addData("/personal/self");
                            session.setAttribute(SessionItem.KEY, si);
                            accountService.setAccountLastLogin(u.getId());
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


    enum Type {
        REGISTER, RESET_PWD
    }

    private void sendValidEmail(String email, Type type, Map<String, String> args) {
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
                    "您在[" + domain + "("+siteService.getString("site.title")+")]上" + title + "，请激活",
                    "<a href='http://" + domain + "/personal/valid?code=" +
                            URLEncoder.encode(encryptHelper.encode(jsonHelper.object2json(args)), "UTF-8")

                            + "' target='_blank'>请点击此链接以" + content + "(" + linkValid + "分钟内有效)</a>。" +
                            "<br/>如果不是您的操作，请忽略该邮件。",
                    true);
        } catch ( UnsupportedEncodingException e) {
            logger.error("不支持编码", e);
        }
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
    @Value("${link.valid}")
    private int linkValid;
    private final static Logger logger = LoggerFactory.getLogger(PersonalController.class);

    public void setLinkValid(int linkValid) {
        this.linkValid = linkValid;
    }

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
