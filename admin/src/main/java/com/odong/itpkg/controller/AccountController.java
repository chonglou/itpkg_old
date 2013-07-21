package com.odong.itpkg.controller;

import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.Group;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.form.uc.BindForm;
import com.odong.itpkg.form.uc.CompanyForm;
import com.odong.itpkg.form.uc.GroupForm;
import com.odong.itpkg.form.uc.UserForm;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.service.RbacService;
import com.odong.itpkg.util.JsonHelper;
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
 *
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
        Map<Long, Group> groups = new HashMap<>();
        Map<Long, User> users = new HashMap<>();
        Map<Long,Contact> contacts = new HashMap<>();
        for (Group g : accountService.listGroup(si.getCompanyId())) {
            groups.put(g.getId(), g);
        }
        for (User u : accountService.listUser(si.getCompanyId())) {
            users.put(u.getId(), u);
            contacts.put(u.getId(), jsonHelper.json2object(u.getContact(), Contact.class));
        }

        map.put("groups", groups);
        map.put("users", users);
        map.put("contacts", contacts);
        map.put("groupUsers", accountService.listGroupUser(si.getCompanyId()));
        map.put("company", accountService.getCompany(si.getCompanyId()));

        return "uc/company";
    }

    @RequestMapping(value = "/company/bind", method = RequestMethod.GET)
    @ResponseBody
    Form getGroupUserBindForm(@ModelAttribute(SessionItem.KEY) SessionItem si){
        Form fm = new Form("bind", "关联用户和组", "/uc/company/bind");
        SelectField<Long> groups = new SelectField<>("group", "用户组");
        SelectField<Long> users = new SelectField<>("user", "用户");
        for(Group g : accountService.listGroup(si.getCompanyId())){
                groups.addOption(g.getName(), g.getId());
        }
        for(User u : accountService.listUser(si.getCompanyId())){
            users.addOption(String.format("%s[%s]", u.getEmail(), u.getUsername()), u.getId());
        }
        RadioField<Boolean> bind = new RadioField<Boolean>("bind", "关联", false);
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
    ResponseItem postBind(@Valid BindForm form, BindingResult result,@ModelAttribute(SessionItem.KEY) SessionItem si){
        ResponseItem ri = formHelper.check(result);
        if(ri.isOk()){
            Group g = accountService.getGroup(form.getGroup());
            User u = accountService.getUser(form.getUser());
            if(u!=null && g!=null && si.getCompanyId().equals(g.getCompany()) && si.getCompanyId().equals(u.getCompany())){
                accountService.setUserGroup(form.getUser(), form.getGroup(), form.isBind());
                logService.add(si.getUserId(), "绑定用户["+u.getUsername()+"]到用户组["+g.getName()+"]", Log.Type.INFO);
            }
            else {
                ri.setOk(false);
                ri.addData("用户["+form.getUser()+"]或组["+form.getGroup()+"]不存在");
            }

        }
        return ri;
    }


        @RequestMapping(value = "/company/group", method = RequestMethod.GET)
    @ResponseBody
    Form getGroupAddForm(@ModelAttribute(SessionItem.KEY) SessionItem si){
        Form fm = new Form("addGroup", "添加用户组", "/uc/company/group");
        fm.addField(new HiddenField<>("id", null));
        fm.addField(new TextField<String>("name", "名称"));
        fm.addField(new TextField<String>("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/company/group/{id}", method = RequestMethod.GET)
    @ResponseBody
    Form getGroupEditForm(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si){
        Group g = accountService.getGroup(id);

        Form fm = new Form("editGroup", "修改用户组", "/uc/company/group");
        if(si.getCompanyId().equals(g.getCompany())){
        fm.addField(new HiddenField<>("id", id));
        fm.addField(new TextField<>("name", "名称", g.getName()));
        fm.addField(new TextField<>("details", "详情", g.getDetails()));
        fm.setOk(true);
        }
        return fm;
    }

    @RequestMapping(value = "/company/group", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postGroup(@Valid GroupForm form, BindingResult result,@ModelAttribute(SessionItem.KEY) SessionItem si){
        ResponseItem ri = formHelper.check(result);
        if(ri.isOk()){
            if(form.getId() == null){
                accountService.addGroup(si.getCompanyId(), form.getName(), form.getDetails());
                logService.add(si.getUserId(), "添加用户组["+form.getName()+"]", Log.Type.INFO);
            }
            else {
                Group g = accountService.getGroup(form.getId());
                if(g != null && g.getCompany().equals(si.getCompanyId())){
                    accountService.setGroup(form.getId(),form.getName(),form.getDetails());
                    logService.add(si.getUserId(), "修改用户组["+form.getId()+"]信息", Log.Type.INFO);
                }
                else {
                    ri.setOk(false);
                    ri.addData("用户组["+form.getId()+"]不存在");
                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/company/group/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delGroup(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si){
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Group g = accountService.getGroup(id);
        if(g != null && si.getCompanyId().equals(g.getCompany())){
            accountService.delGroup(id);
            logService.add(si.getUserId(), "删除用户组["+g.getName()+"]", Log.Type.INFO);
        }
        else {
            ri.addData("用户组["+id+"]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/company/user", method = RequestMethod.GET)
    @ResponseBody
    Form getUser(@ModelAttribute(SessionItem.KEY) SessionItem si){
        Form fm = new Form("addUser", "添加用户", "/uc/company/user");
        fm.addField(new TextField<>("email", "邮箱"));
        fm.addField(new TextField<>("username", "用户名"));
        fm.addField(new TextField<>("password", "密码"));
        fm.setOk(true);
        return fm;
    }


    @RequestMapping(value = "/company/user", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postUser(@Valid UserForm form, BindingResult result,@ModelAttribute(SessionItem.KEY) SessionItem si){
        ResponseItem ri = formHelper.check(result);
        if(ri.isOk()){
            if(accountService.getUser(form.getEmail()) == null){
                accountService.addUser(si.getCompanyId(), form.getEmail(), form.getUsername(), form.getPassword());
                logService.add(si.getUserId(), "添加用户["+form.getEmail()+"]", Log.Type.INFO);
            }
            else {
                ri.setOk(false);
                ri.addData("账户["+form.getEmail()+"]已存在");
            }
        }
        return ri;
    }

    @RequestMapping(value = "/company/user/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delUser(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si){
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        User u = accountService.getUser(id);
        if(u != null && u.getState()!= User.State.DONE && si.getCompanyId().equals(u.getCompany())){
            accountService.setUserState(id, User.State.DONE);
            logService.add(si.getUserId(), "删除用户["+u.getUsername()+"]", Log.Type.INFO);
        }
        else {
            ri.addData("用户["+id+"]不存在");
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
            logService.add(si.getUserId(), "更新公司信息", Log.Type.INFO);
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
