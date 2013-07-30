package com.odong.itpkg.controller.uc;

import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.form.uc.UserForm;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.util.JsonHelper;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.HiddenField;
import com.odong.portal.web.form.TextAreaField;
import com.odong.portal.web.form.TextField;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午12:46
 */
@Controller("c.uc.user")
@RequestMapping(value = "/uc/user")
@SessionAttributes(SessionItem.KEY)
public class UserController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getUser(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        Map<Long, Contact> contactMap = new HashMap<>();
        List<User> userList = accountService.listUserByCompany(si.getSsCompanyId());
        for (User u : userList) {
            contactMap.put(u.getId(), jsonHelper.json2object(u.getContact(), Contact.class));
        }
        map.put("userList", userList);
        map.put("contactMap", contactMap);
        return "uc/user";
    }


    @RequestMapping(value = "/add", method = RequestMethod.GET)
    @ResponseBody
    Form getUserAddForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("user", "添加用户", "/uc/user/");
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
        for (int i = 0; i < fields.length; i += 2) {
            TextField tf = new TextField(fields[i], fields[i + 1]);
            tf.setRequired(false);
            fm.addField(tf);
        }

        fm.addField(new TextAreaField("details", "详情"));

        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    Form getUserAddForm(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        User u = accountService.getUser(id);
        Form fm = new Form("user", "修改用户[" + id + "]", "/uc/user/");
        if (u != null && si.getSsCompanyId().equals(u.getCompany())) {
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
            for (int i = 0; i < fields.length; i += 3) {
                TextField<String> tf = new TextField<String>(fields[i], fields[i + 1], fields[i + 2]);
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


    @RequestMapping(value = "/", method = RequestMethod.POST)
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
                accountService.addUser(form.getUsername(), form.getUnit(), c, si.getSsCompanyId());
                logService.add(si.getSsAccountId(), "添加用户[" + form.getUsername() + "]", Log.Type.INFO);

            } else {
                User user = accountService.getUser(form.getUser());
                if (user == null || !user.getCompany().equals(si.getSsCompanyId())) {
                    ri.setOk(false);
                    ri.addData("用户[" + form.getUser() + "]不存在");

                } else {
                    accountService.setUserInfo(user.getId(), form.getUsername(), form.getUnit(), c);
                    logService.add(si.getSsAccountId(), "更新用户[" + user.getId() + "]信息", Log.Type.INFO);

                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem delUser(@PathVariable long id, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);

        User u = accountService.getUser(id);
        if (u != null && si.getSsCompanyId().equals(u.getCompany())) {
            accountService.delUser(id);
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "删除用户[" + u.getUsername() + "]", Log.Type.INFO);
        } else {
            ri.addData("用户[" + id + "]不存在");
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
    private JsonHelper jsonHelper;

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }

    public void setJsonHelper(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
    }
}
