package com.odong.itpkg.controller.personal;

import com.odong.itpkg.entity.uc.Account;
import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.personal.LoginForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.service.RbacService;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.PasswordField;
import com.odong.portal.web.form.TextField;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午1:04
 */
@Controller("c.personal.login")
@RequestMapping(value = "/personal")
@SessionAttributes(SessionItem.KEY)
public class LoginController {


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
                            si.setSsUsername(u.getUsername());
                            si.setSsEmail(u.getEmail());
                            si.setSsAccountId(u.getId());
                            si.setSsCompanyId(u.getCompany());
                            si.setSsAdmin(rbacService.authAdmin(u.getId()));
                            si.setSsCompanyManager(rbacService.authCompany(u.getId(), u.getCompany(), RbacService.OperationType.MANAGE));
                            si.setSsCreated(new Date());

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


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ResponseBody
    ResponseItem getLogout(HttpSession session) {
        SessionItem si = (SessionItem) session.getAttribute(SessionItem.KEY);
        logService.add(si.getSsAccountId(), "注销登陆", Log.Type.INFO);
        session.invalidate();
        ResponseItem ri = new ResponseItem(ResponseItem.Type.redirect);
        ri.addData("/");
        ri.setOk(true);
        return ri;
    }

    @Resource
    private SiteService siteService;
    @Resource
    private RbacService rbacService;
    @Resource
    private AccountService accountService;
    @Resource
    private FormHelper formHelper;
    @Resource
    private LogService logService;

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setRbacService(RbacService rbacService) {
        this.rbacService = rbacService;
    }
}
