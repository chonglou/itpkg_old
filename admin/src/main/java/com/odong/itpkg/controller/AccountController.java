package com.odong.itpkg.controller;

import com.odong.itpkg.entity.uc.*;
import com.odong.itpkg.form.uc.*;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.service.RbacService;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.*;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:28
 */
@Controller
@RequestMapping(value = "/uc")
@SessionAttributes(SessionItem.KEY)
public class AccountController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getList(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Map<Long, Group> groupMap = new HashMap<>();
        Map<Long, User> userMap = new HashMap<>();
        Map<Long, Account> accountMap = new HashMap<>();
        Map<Long, Contact> contactMap = new HashMap<>();
        for (Group g : accountService.listGroup(si.getCompanyId())) {
            groupMap.put(g.getId(), g);
        }
        for (User u : accountService.listUserByCompany(si.getCompanyId())) {
            userMap.put(u.getId(), u);
            contactMap.put(u.getId(), jsonHelper.json2object(u.getContact(), Contact.class));
        }
        for (Account a : accountService.listAccount(si.getCompanyId())) {
            accountMap.put(a.getId(), a);
            contactMap.put(a.getId(), jsonHelper.json2object(a.getContact(), Contact.class));
        }

        map.put("groupMap", groupMap);
        map.put("userMap", userMap);
        map.put("accountMap", accountMap);
        map.put("contactMap", contactMap);
        map.put("groupUserList", accountService.listGroupUser(si.getCompanyId()));
        map.put("company", accountService.getCompany(si.getCompanyId()));


        return "uc/company";
    }

    @RequestMapping(value = "/company/bind", method = RequestMethod.GET)
    @ResponseBody
    Form getGroupUserBindForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("bind", "关联用户和组", "/uc/company/bind");
        SelectField<Long> groups = new SelectField<>("group", "用户组");
        SelectField<Long> users = new SelectField<>("user", "用户");
        for (Group g : accountService.listGroup(si.getCompanyId())) {
            groups.addOption(g.getName(), g.getId());
        }
        for (User u : accountService.listUserByCompany(si.getCompanyId())) {
            users.addOption(String.format(u.getUsername()), u.getId());
        }
        RadioField<Boolean> bind = new RadioField<>("bind", "关联", false);
        bind.addOption("建立", true);
        bind.addOption("解除", false);
        fm.addField(new TextField<String>("name", "名称"));
        fm.addField(new TextField<String>("details", "详情"));
        fm.addField(bind);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/company/bind", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postBind(@Valid BindForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            Group g = accountService.getGroup(form.getGroup());
            User u = accountService.getUser(form.getUser());
            if (u != null && g != null && si.getCompanyId().equals(g.getCompany()) && si.getCompanyId().equals(u.getCompany())) {
                accountService.setUserGroup(form.getUser(), form.getGroup(), form.isBind());
                logService.add(si.getAccountId(), "绑定用户[" + u.getUsername() + "]到用户组[" + g.getName() + "]", Log.Type.INFO);
            } else {
                ri.setOk(false);
                ri.addData("用户[" + form.getUser() + "]或组[" + form.getGroup() + "]不存在");
            }

        }
        return ri;
    }


    @RequestMapping(value = "/company/group", method = RequestMethod.GET)
    @ResponseBody
    Form getGroupAddForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("addGroup", "添加用户组", "/uc/company/group");
        fm.addField(new HiddenField<>("id", null));
        fm.addField(new TextField<String>("name", "名称"));
        fm.addField(new TextField<String>("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/company/setAccount", method = RequestMethod.GET)
    @ResponseBody
    Form getAccountSetForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("setAccount", "添加账户", "/uc/company/setAccount");
        SelectField<Long> account = new SelectField<Long>("account", "账户");
        for(Account a : accountService.listAccount(si.getCompanyId())){
            if(a.getState() != Account.State.SUBMIT){
            account.addOption(String.format("%s[%s]", a.getEmail(), a.getUsername()), a.getId());
            }
        }
        RadioField<Boolean> enable = new RadioField<Boolean>("enable", "状态", false);
        enable.addOption("启用", true);
        enable.addOption("禁用", false);
        fm.addField(account);
        fm.addField(enable);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/company/setAccount", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem  postAccountSetForm(@Valid AccountSetForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if(ri.isOk()){
            Account a = accountService.getAccount(form.getAccount());
            if(a != null && a.getCompany().equals(si.getCompanyId())){
                accountService.setAccountState(a.getId(), form.isEnable() ? Account.State.ENABLE : Account.State.DISABLE);
                logService.add(si.getAccountId(), (form.isEnable() ? "启用":"禁用")+"账户["+a.getEmail()+"]", Log.Type.INFO);
            }
        }
        else {
            ri.addData("用户["+form.getAccount()+"]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/company/addAccount", method = RequestMethod.GET)
    @ResponseBody
    Form getAccountAddForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("addAccount", "添加账户", "/uc/company/addAccount");

        fm.addField(new TextField<>("email", "名称"));
        fm.addField(new TextField<>("username", "名称"));
        fm.addField(new TextField<>("password", "详情"));
        fm.setOk(true);

        return fm;
    }

    @RequestMapping(value = "/company/addAccount", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postAccountAddForm(@Valid AccountAddForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (!siteService.getBoolean("site.allowRegister")) {
            ri.setOk(false);
            ri.addData("站点禁止注册新账户");
        }
        if (ri.isOk()) {
            if (accountService.getAccount(form.getEmail()) == null) {
                accountService.addAccount(si.getCompanyId(), form.getEmail(), form.getUsername(), form.getPassword());
                Account a = accountService.getAccount(form.getEmail());
                rbacService.bindCompany(a.getId(), si.getCompanyId(), RbacService.OperationType.USE, true);
                logService.add(si.getAccountId(), "添加新用户[" + form.getEmail() + "] 并赋予USE权限", Log.Type.INFO);
            } else {
                ri.setOk(false);
                ri.addData("邮箱[" + form.getEmail() + "]已存在");
            }
        }
        return ri;
    }

    @RequestMapping(value = "/company/group/{id}", method = RequestMethod.GET)
    @ResponseBody
    Form getGroupEditForm(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Group g = accountService.getGroup(id);

        Form fm = new Form("editGroup", "修改用户组", "/uc/company/group");
        if (si.getCompanyId().equals(g.getCompany())) {
            fm.addField(new HiddenField<>("id", id));
            fm.addField(new TextField<>("name", "名称", g.getName()));
            fm.addField(new TextField<>("details", "详情", g.getDetails()));
            fm.setOk(true);
        } else {
            fm.addData("用户组[" + id + "]不存在");
        }
        return fm;
    }

    @RequestMapping(value = "/company/group", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postGroup(@Valid GroupForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            if (form.getId() == null) {
                accountService.addGroup(si.getCompanyId(), form.getName(), form.getDetails());
                logService.add(si.getAccountId(), "添加用户组[" + form.getName() + "]", Log.Type.INFO);
            } else {
                Group g = accountService.getGroup(form.getId());
                if (g != null && g.getCompany().equals(si.getCompanyId())) {
                    accountService.setGroup(form.getId(), form.getName(), form.getDetails());
                    logService.add(si.getAccountId(), "修改用户组[" + form.getId() + "]信息", Log.Type.INFO);
                } else {
                    ri.setOk(false);
                    ri.addData("用户组[" + form.getId() + "]不存在");
                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/company/group/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delGroup(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Group g = accountService.getGroup(id);
        if (g != null && si.getCompanyId().equals(g.getCompany())) {
            accountService.delGroup(id);
            logService.add(si.getAccountId(), "删除用户组[" + g.getName() + "]", Log.Type.INFO);
        } else {
            ri.addData("用户组[" + id + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/company/user", method = RequestMethod.GET)
    @ResponseBody
    Form getUserAddForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("addUser", "添加用户", "/uc/company/user");
        fm.addField(new HiddenField<Long>("user", null));
        fm.addField(new TextField<>("username", "用户名"));
        fm.addField(new TextField<>("qq", "QQ号"));
        fm.addField(new TextField<>("tel", "电话"));
        fm.addField(new TextField<>("fax", "传真"));
        fm.addField(new TextField<>("address", "地址"));
        fm.addField(new TextField<>("weixin", "微信"));
        fm.addField(new TextField<>("web", "网站"));
        fm.addField(new TextField<>("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/company/user/{id}", method = RequestMethod.GET)
    @ResponseBody
    Form getUserAddForm(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        User u = accountService.getUser(id);
        Form fm = new Form("editUser", "修改用户[" + id + "]", "/uc/company/user");
        if (u != null && si.getCompanyId().equals(u.getCompany())) {
            Contact c = jsonHelper.json2object(u.getContact(), Contact.class);
            fm.addField(new HiddenField<>("user", id));
            fm.addField(new TextField<>("username", "用户名", u.getUsername()));
            fm.addField(new TextField<>("qq", "QQ号", c.getQq()));
            fm.addField(new TextField<>("tel", "电话", c.getTel()));
            fm.addField(new TextField<>("fax", "传真", c.getFax()));
            fm.addField(new TextField<>("address", "地址", c.getAddress()));
            fm.addField(new TextField<>("weixin", "微信", c.getWeixin()));
            fm.addField(new TextField<>("web", "网站", c.getWeb()));
            fm.addField(new TextField<>("details", "详情", c.getDetails()));
            fm.setOk(true);
        } else {
            fm.addData("用户[" + id + "]不存在");
        }
        return fm;
    }


    @RequestMapping(value = "/company/user", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postUserAddForm(@Valid UserForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            Contact c = new Contact();
            c.setFax(form.getFax());
            c.setWeixin(form.getFax());
            c.setDetails(form.getDetails());
            c.setTel(form.getTel());
            c.setWeb(form.getWeb());
            c.setAddress(form.getAddress());
            c.setQq(form.getQq());

            if (form.getUser() == null) {
                accountService.addUser(form.getUsername(), c, si.getCompanyId());
                logService.add(si.getAccountId(), "添加用户[" + form.getUsername() + "]", Log.Type.INFO);

            } else {
                User user = accountService.getUser(form.getUser());
                if (user == null) {
                    ri.setOk(false);
                    ri.addData("用户[" + form.getUser() + "]不存在");

                } else {
                    accountService.setUserInfo(user.getId(), form.getUsername(), c);
                    logService.add(si.getAccountId(), "更新用户[" + user.getId() + "]信息", Log.Type.INFO);

                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/company/user/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delUser(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        User u = accountService.getUser(id);
        if (u != null && si.getCompanyId().equals(u.getCompany())) {
            accountService.delUser(id);
            logService.add(si.getAccountId(), "删除用户[" + u.getUsername() + "]", Log.Type.INFO);
        } else {
            ri.addData("用户[" + id + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/company/info", method = RequestMethod.GET)
    @ResponseBody
    Form postCompanyInfo(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("companyInfo", "公司信息", "/uc/company/info");

        Company c = accountService.getCompany(si.getCompanyId());

        fm.addField(new TextField<>("name", "名称", c.getName()));
        TextAreaField taf = new TextAreaField("details", "详情", c.getDetails());
        taf.setHtml(true);
        fm.addField(taf);
        fm.setOk(true);

        return fm;
    }

    @RequestMapping(value = "/company/info", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postCompanyInfo(@Valid CompanyForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);

        if (ri.isOk()) {
            accountService.setCompanyInfo(si.getCompanyId(), form.getName(), form.getDetails());
            ri.setOk(true);
            logService.add(si.getAccountId(), "更新公司信息", Log.Type.INFO);
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
    private JsonHelper jsonHelper;
    @Resource
    private SiteService siteService;

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }

    public void setRbacService(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }
}
