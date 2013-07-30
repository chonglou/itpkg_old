package com.odong.itpkg.controller.admin;

import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.admin.CompanyStateForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
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
 * Time: 下午12:51
 */
@Controller("c.admin.company")
@RequestMapping(value = "/admin/company")
@SessionAttributes(SessionItem.KEY)
public class CompanyController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getCompanyList(Map<String, Object> map) {
        map.put("companyList", accountService.listCompany());
        return "admin/company";
    }


    @RequestMapping(value = "/state", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postCompany(@Valid CompanyStateForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);

        if (si.getSsCompanyId().equals(form.getCompany())) {
            ri.addData("管理员公司");
            ri.setOk(false);
        }

        if (ri.isOk()) {
            accountService.setCompanyState(form.getCompany(), form.getState());
            ri.setOk(true);
            logService.add(si.getSsAccountId(), "设置公司[" + form.getCompany() + "]状态[" + form.getState() + "]", Log.Type.INFO);
        }
        return ri;
    }
    @Resource
    private AccountService accountService;
    @Resource
    private LogService logService;
    @Resource
    private FormHelper formHelper;

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }
}
