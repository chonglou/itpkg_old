package com.odong.itpkg.controller;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.entity.uc.Account;
import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.form.net.HostForm;
import com.odong.itpkg.form.uc.AccountAddForm;
import com.odong.itpkg.form.uc.AccountSetForm;
import com.odong.itpkg.form.uc.CompanyForm;
import com.odong.itpkg.form.uc.UserForm;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.HostService;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:28
 */
@Controller
@RequestMapping(value = "/company")
@SessionAttributes(SessionItem.KEY)
public class CompanyController {
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    String getInfo(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        map.put("company", accountService.getCompany(si.getCompanyId()));
        return "company/info";
    }

    @RequestMapping(value = "/host", method = RequestMethod.GET)
    String getHost(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        map.put("hostList", hostService.listHost(si.getCompanyId()));
        return "company/host";
    }

    @RequestMapping(value = "/limit", method = RequestMethod.GET)
    String getLimit(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        map.put("dateLimitList", hostService.listFirewallDateLimit(si.getCompanyId()));
        map.put("flowLimitList", hostService.listFirewallFlowLimit(si.getCompanyId()));
        return "company/limit";
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    String getUser(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Map<Long, Contact> contactMap = new HashMap<>();
        List<User> userList = accountService.listUserByCompany(si.getCompanyId());
        for (User u : userList) {
            contactMap.put(u.getId(), jsonHelper.json2object(u.getContact(), Contact.class));
        }
        map.put("userList", userList);
        map.put("contactMap", contactMap);
        return "company/user";
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    String getAccount(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        map.put("accountList", accountService.listAccount(si.getCompanyId()));
        return "company/account";
    }

    @RequestMapping(value = "/manage/account", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postAccountSetForm(@Valid AccountSetForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (ri.isOk()) {
            if (form.getAccount() == si.getAccountId()) {
                ri.setOk(false);
                ri.addData("不能删除自己");
            } else {
                Account a = accountService.getAccount(form.getAccount());
                if (a != null && a.getCompany().equals(si.getCompanyId()) && a.getState() != Account.State.SUBMIT) {
                    accountService.setAccountState(form.getAccount(), form.getState());
                    logService.add(si.getAccountId(), form.getState() + "账户[" + a.getEmail() + "]", Log.Type.INFO);
                } else {
                    ri.setOk(false);
                    ri.addData("用户[" + form.getAccount() + "]不存在");
                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/manage/account/add", method = RequestMethod.GET)
    @ResponseBody
    Form getAccountAddForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("addAccount", "添加账户", "/company/manage/account/add");

        fm.addField(new TextField<>("email", "邮箱"));
        fm.addField(new TextField<>("username", "用户名"));
        fm.addField(new TextField<>("password", "密码"));
        fm.setOk(true);

        return fm;
    }

    @RequestMapping(value = "/manage/account/add", method = RequestMethod.POST)
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
                ri.addData("账户[" + form.getEmail() + "]已存在");
            }
        }
        return ri;
    }


    @RequestMapping(value = "/manage/info", method = RequestMethod.GET)
    @ResponseBody
    Form postCompanyInfo(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("companyInfo", "公司信息", "/company/manage/info");

        Company c = accountService.getCompany(si.getCompanyId());

        fm.addField(new TextField<>("name", "名称", c.getName()));
        TextAreaField taf = new TextAreaField("details", "详情", c.getDetails());
        taf.setHtml(true);
        fm.addField(taf);
        fm.setOk(true);

        return fm;
    }

    @RequestMapping(value = "/manage/info", method = RequestMethod.POST)
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

    @RequestMapping(value = "/user/add", method = RequestMethod.GET)
    @ResponseBody
    Form getUserAddForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("user", "添加用户", "/company/user");
        fm.addField(new HiddenField<Long>("user", null));
        fm.addField(new TextField<>("username", "用户名"));

        String[] fields = new String[]{
                "unit", "部门",
                "qq", "QQ号",
                "tel", "电话",
                "fax", "传真",
                "address", "地址",
                "weixin", "微信",
                "web", "网站"
        };
        for(int i=0; i<fields.length; i+=2)
        {
            TextField tf = new TextField(fields[i], fields[i+1]);
            tf.setRequired(false);
            fm.addField(tf);
        }

        fm.addField(new TextAreaField("details", "详情"));

        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    @ResponseBody
    Form getUserAddForm(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        User u = accountService.getUser(id);
        Form fm = new Form("user", "修改用户[" + id + "]", "/company/user");
        if (u != null && si.getCompanyId().equals(u.getCompany())) {
            Contact c = jsonHelper.json2object(u.getContact(), Contact.class);
            fm.addField(new HiddenField<>("user", u.getId()));
            fm.addField(new TextField<>("username", "用户名", u.getUsername()));

            String[] fields = new String[]{
                    "unit", "部门", u.getUnit(),
                    "qq", "QQ号", c.getQq(),
                    "tel", "电话", c.getTel(),
                    "fax", "传真", c.getFax(),
                    "address", "地址", c.getAddress(),
                    "weixin", "微信", c.getWeixin(),
                    "web", "网站", c.getWeb()
            };
            for(int i=0;i<fields.length;i+=3){
                TextField<String> tf = new TextField<String>(fields[i], fields[i+1], fields[i+2]);
                tf.setRequired(false);
                fm.addField(tf);
            }
            fm.addField(new TextAreaField("details", "详情", c.getDetails()));
            fm.setOk(true);
        } else {
            fm.addData("用户[" + id + "]不存在");
        }
        return fm;
    }


    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postUserForm(@Valid UserForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
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
                accountService.addUser(form.getUsername(), form.getUnit(), c, si.getCompanyId());
                logService.add(si.getAccountId(), "添加用户[" + form.getUsername() + "]", Log.Type.INFO);

            } else {
                User user = accountService.getUser(form.getUser());
                if (user == null || !user.getCompany().equals(si.getCompanyId())) {
                    ri.setOk(false);
                    ri.addData("用户[" + form.getUser() + "]不存在");

                } else {
                    accountService.setUserInfo(user.getId(), form.getUsername(), form.getUnit(), c);
                    logService.add(si.getAccountId(), "更新用户[" + user.getId() + "]信息", Log.Type.INFO);

                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delUser(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);

        User u = accountService.getUser(id);
        if (u != null && si.getCompanyId().equals(u.getCompany())) {
            accountService.delUser(id);
            ri.setOk(true);
            logService.add(si.getAccountId(), "删除用户[" + u.getUsername() + "]", Log.Type.INFO);
        } else {
            ri.addData("用户[" + id + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/host/{host}", method = RequestMethod.GET)
    @ResponseBody
    Form getHostAddForm(@PathVariable long host, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("host", "修改主机["+host+"]", "/company/host");
        Host h = hostService.getHost(host);
        if(h != null && h.getCompany().equals(si.getCompanyId())){
            Ip wanIp = hostService.getIp(h.getWanIp());
            fm.addField(new HiddenField<>("id", host));
            SelectField<Ip.Type> type = new SelectField<Ip.Type>("type", "类型", wanIp.getType());
            type.addOption("固定IP", Ip.Type.STATIC);
            type.addOption("动态分配", Ip.Type.DHCP);
            type.addOption("拨号上网", Ip.Type.PPPOE);
            fm.addField(type);

            String[] fields = new String[]{
                    "name", "名称", h.getName(),
                    "domain", "域", h.getDomain(),
                    "wanMac", "WAN网卡MAC", h.getWanMac(),
                    "address", "IP地址", wanIp.getAddress(),
                    "netmask", "子网掩码", wanIp.getNetmask(),
                    "gateway", "网关", wanIp.getGateway(),
                    "dns1", "DNS1", wanIp.getDns1(),
                    "dns2", "DNS2", wanIp.getDns2(),
                    "lanMac", "LAN网卡MAC", h.getLanMac(),
                    "lanNet", "LAN网段", h.getLanNet()
            };
            for(int i=0; i<fields.length; i+=3){
                fm.addField(new TextField<String>(fields[i], fields[i+1], fields[i+2]));
            }
            fm.addField(new TextField<Integer>("rpcPort", "RPC端口"));

            fm.addField(new TextAreaField("details", "详情"));
            fm.setOk(true);
        }
        else {
            fm.addData("主机["+host+"]不存在");
        }
        return fm;
    }
    @RequestMapping(value = "/host/add", method = RequestMethod.GET)
    @ResponseBody
    Form getHostEditForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("host", "添加主机", "/company/host");
        fm.addField(new HiddenField<Long>("id", null));

        SelectField<Ip.Type> type = new SelectField<Ip.Type>("type", "类型");
        type.addOption("固定IP", Ip.Type.STATIC);
        type.addOption("动态分配", Ip.Type.DHCP);
        type.addOption("拨号上网", Ip.Type.PPPOE);
        fm.addField(type);

        String[] fields = new String[]{
                "name", "名称",
                "domain", "域",
                "wanMac", "WAN网卡MAC",
                "address", "IP地址",
                "netmask", "子网掩码",
                "gateway", "网关",
                "dns1", "DNS1",
                "dns2", "DNS2",
                "lanMac", "LAN网卡MAC",
                "lanNet", "LAN网段"
        };
        for(int i=0; i<fields.length; i+=2){
            fm.addField(new TextField<String>(fields[i], fields[i+1]));
        }
        fm.addField(new TextField<Integer>("rpcPort", "RPC端口"));

        fm.addField(new TextAreaField("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/host", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postHost(@Valid HostForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if(ri.isOk()){
            if(form.getId() == null){
                String wanIpId = UUID.randomUUID().toString();
                switch (form.getType()){
                    case STATIC:
                        hostService.addIpStatic(wanIpId, form.getAddress(),form.getNetmask(),form.getGateway(),form.getDns1(),form.getDns2());
                        break;
                    case PPPOE:
                        hostService.addIpPppoe(wanIpId, form.getUsername(), form.getPassword());
                        break;
                    case DHCP:
                        hostService.addIpDhcp(wanIpId);
                        break;
                }
                hostService.addHost(si.getCompanyId(), form.getName(), form.getDomain(), wanIpId, form.getWanMac(), form.getRpcPort(), form.getLanNet(), form.getLanMac(), form.getDetails());
                logService.add(si.getAccountId(), "添加主机["+form.getName()+"]", Log.Type.INFO);
            }
            else {
                Host h = hostService.getHost(form.getId());
                if(h!=null && h.getCompany().equals(si.getCompanyId())){
                    hostService.setHostDomain(h.getId(), form.getDomain());
                    hostService.setHostInfo(h.getId(), form.getName(), form.getDetails());
                    hostService.setHostLan(h.getId(), form.getLanNet(), form.getLanMac());
                    hostService.setHostWan(h.getId(), form.getRpcPort(), form.getWanMac());
                    switch (form.getType()){
                        case STATIC:
                            hostService.setIpStatic(h.getWanIp(), form.getAddress(), form.getNetmask(), form.getGateway(),form.getDns1(),form.getDns2());
                            break;
                        case DHCP:
                            hostService.setIpDhcp(h.getWanIp());
                            break;
                        case PPPOE:
                            hostService.setIpPppoe(h.getWanIp(), form.getUsername(), form.getPassword());
                            break;
                    }
                    logService.add(si.getAccountId(), "更新主机["+h.getId()+"]信息", Log.Type.INFO);

                }
                else {
                    ri.setOk(false);
                    ri.addData("主机[" + form.getId() + "]不存在");
                }
            }
        }
        return ri;
    }

    /*

 @RequestMapping(value = "/group/{group}", method = RequestMethod.GET)
    String getGroup(@PathVariable long group, Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Group g = accountService.getGroup(group);
        if(g != null && g.getCompany().equals(si.getCompanyId())){
            map.put("group", g);
            map.put("userList", accountService.listUserByGroup(group));
        }
        return "company/group";
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
    */


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
    @Resource
    private HostService hostService;

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }

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
