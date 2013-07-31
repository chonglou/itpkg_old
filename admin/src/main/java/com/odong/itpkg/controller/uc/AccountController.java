package com.odong.itpkg.controller.uc;

import com.odong.itpkg.entity.uc.Account;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.uc.AccountAddForm;
import com.odong.itpkg.form.uc.AccountSetForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.service.RbacService;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.TextField;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午12:43
 */
@Controller("c.uc.account")
@RequestMapping(value = "/uc/account")
@SessionAttributes(SessionItem.KEY)
public class AccountController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getAccount(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        map.put("accountList", accountService.listAccount(si.getSsCompanyId()));
        return "uc/account";
    }

    @RequestMapping(value = "/state", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postAccountSetForm(@Valid AccountSetForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (form.getAccount() == si.getSsAccountId()) {
            ri.setOk(false);
            ri.addData("不能自杀");
        }
        if (!si.isSsCompanyManager()) {
            ri.setOk(false);
            ri.addData("您不是公司管理员");
        }
        Account a = accountService.getAccount(form.getAccount());
        if (a != null && a.getCompany().equals(si.getSsCompanyId())) {
            switch (a.getState()) {
                case ENABLE:
                    if (form.getState() != Account.State.DISABLE) {
                        ri.setOk(false);
                        ri.addData("账户只能被禁用");
                    }
                    break;
                case DISABLE:
                    if (form.getState() != Account.State.ENABLE) {
                        ri.setOk(false);
                        ri.addData("账户只能被启用");
                    }
                    break;
                case SUBMIT:
                    if (form.getState() == Account.State.DONE) {
                        accountService.delAccount(form.getAccount());
                        return ri;
                    }
                    ri.setOk(false);
                    ri.addData("账户只能被删除");
                    break;
                default:
                    ri.setOk(false);
                    ri.addData("账户不存在");
                    break;
            }
        } else {
            ri.setOk(false);
            ri.addData("用户[" + form.getAccount() + "]不存在");
        }

        if (ri.isOk()) {
            accountService.setAccountState(form.getAccount(), form.getState());
            logService.add(si.getSsAccountId(), "账户[" + form.getAccount() + "] => " + form.getState(), Log.Type.INFO);
        }
        return ri;
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
    Form getAccountAddForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("addAccount", "添加账户", "/uc/account/add");
        if (si.isSsCompanyManager()) {
            fm.addField(new TextField<>("email", "邮箱"));
            fm.addField(new TextField<>("username", "用户名"));
            fm.addField(new TextField<>("password", "密码"));
            fm.setOk(true);
        } else {
            fm.addData("您不是公司管理员");
        }

        return fm;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postAccountAddForm(@Valid AccountAddForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);

        if (!siteService.getBoolean("site.allowRegister")) {
            ri.setOk(false);
            ri.addData("站点禁止注册新账户");
        }
        if (!si.isSsCompanyManager()) {
            ri.setOk(false);
            ri.addData("您不是公司管理员");
        }
        if (ri.isOk()) {
            if (accountService.getAccount(form.getEmail()) == null) {
                accountService.addAccount(si.getSsCompanyId(), form.getEmail(), form.getUsername(), form.getPassword());
                Account a = accountService.getAccount(form.getEmail());
                rbacService.bindCompany(a.getId(), si.getSsCompanyId(), RbacService.OperationType.USE, true);
                logService.add(si.getSsAccountId(), "添加新用户[" + form.getEmail() + "] 并赋予USE权限", Log.Type.INFO);
            } else {
                ri.setOk(false);
                ri.addData("账户[" + form.getEmail() + "]已存在");
            }
        }
        return ri;
    }

    @Resource
    private AccountService accountService;
    @Resource
    private LogService logService;
    @Resource
    private FormHelper formHelper;
    @Resource
    private RbacService rbacService;
    @Resource
    private SiteService siteService;

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }

    public void setRbacService(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
