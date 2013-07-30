package com.odong.itpkg.controller.uc;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.entity.uc.Account;
import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.entity.uc.User;
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
 * Date: 13-7-18
 * Time: 上午11:28
 */
@Controller("c.uc.company")
@RequestMapping(value = "/uc/company")
@SessionAttributes(SessionItem.KEY)
public class CompanyController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getInfo(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        map.put("company", accountService.getCompany(si.getSsCompanyId()));
        return "uc/company";
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    Form postCompanyInfo(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        Form fm = new Form("companyInfo", "公司信息", "/uc/company/info");
        if(si.isSsCompanyManager()){
        Company c = accountService.getCompany(si.getSsCompanyId());

        fm.addField(new TextField<>("name", "名称", c.getName()));
        TextAreaField taf = new TextAreaField("details", "详情", c.getDetails());
        taf.setHtml(true);
        fm.addField(taf);
        fm.setOk(true);
        }
        else {
            fm.addData("您不是公司管理员");
        }

        return fm;
    }

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postCompanyInfo(@Valid CompanyForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if(!si.isSsCompanyManager()){
            ri.setOk(false);
            ri.addData("您不是公司管理员");
        }
        if (ri.isOk()) {
            accountService.setCompanyInfo(si.getSsCompanyId(), form.getName(), form.getDetails());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "更新公司信息", Log.Type.INFO);
        }
        return ri;
    }

    @Resource
    private AccountService accountService;
    @Resource
    private LogService logService;
    @Resource
    private FormHelper formHelper;

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
