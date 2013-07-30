package com.odong.itpkg.controller.personal;

import com.odong.itpkg.entity.uc.Account;
import com.odong.itpkg.form.personal.ResetPwdForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
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
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午1:01
 */

@Controller("c.personal.resetPwd")
@RequestMapping(value = "/personal")
@SessionAttributes(SessionItem.KEY)
public class ResetPasswordController extends EmailController{

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
                ri.addData("已向您的邮箱发送了密码重置链接，请进入邮箱进行操作。");

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
    @Resource
    private FormHelper formHelper;
    @Resource
    private AccountService accountService;

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
