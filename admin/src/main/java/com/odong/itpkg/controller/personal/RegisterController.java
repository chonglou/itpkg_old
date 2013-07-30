package com.odong.itpkg.controller.personal;

import com.odong.itpkg.entity.uc.Account;
import com.odong.itpkg.form.personal.ActiveForm;
import com.odong.itpkg.form.personal.RegisterForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.AgreeField;
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
import javax.validation.Valid;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午1:01
 */
@Controller("c.personal/register")
@RequestMapping(value = "/personal")
@SessionAttributes(SessionItem.KEY)
public class RegisterController extends EmailController {


    @RequestMapping(value = "/active", method = RequestMethod.GET)
    @ResponseBody
    Form getActive() {
        Form fm = new Form("active", "激活账户", "/personal/active");
        fm.addField(new TextField("email", "邮箱"));
        fm.addField(new AgreeField("agree", "用户协议", siteService.getString("site.regProtocol")));
        fm.setCaptcha(true);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/active", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postActive(@Valid ActiveForm form, BindingResult result, HttpServletRequest request) {
        ResponseItem ri = formHelper.check(result, request, true);
        if(!form.isAgree()){
            ri.setOk(false);
            ri.addData("您需要同意用户协议才能继续");
        }
        if (ri.isOk()) {
            Account a = accountService.getAccount(form.getEmail());
            if (a != null && a.getState() == Account.State.SUBMIT) {
                sendValidEmail(form.getEmail(), Type.REGISTER, new HashMap<String, String>());
                ri.addData("已向您的邮箱发送了账户激活链接，请进入邮箱进行操作。");
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
        fm.addField(new AgreeField("agree", "用户协议", siteService.getString("site.regProtocol")));
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
        if(!form.isAgree()){
            ri.setOk(false);
            ri.addData("您需要同意用户协议才能继续");
        }
        if (ri.isOk()) {
            Account u = accountService.getAccount(form.getEmail());
            if (u == null) {
                String companyId = UUID.randomUUID().toString();
                accountService.addCompany(companyId, form.getCompany(), "暂无");
                accountService.addAccount(companyId, form.getEmail(), form.getUsername(), form.getNewPwd());
                sendValidEmail(form.getEmail(), Type.REGISTER, new HashMap<String, String>());
                ri.addData("已向您的邮箱发送一封激活邮件，请进入邮箱继续操作.");
            } else {
                ri.setOk(false);
                ri.addData("邮箱[" + form.getEmail() + "]已存在");
            }
        }
        return ri;
    }

    @Resource
    private AccountService accountService;
    @Resource
    private FormHelper formHelper;
    @Resource
    private SiteService siteService;

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
