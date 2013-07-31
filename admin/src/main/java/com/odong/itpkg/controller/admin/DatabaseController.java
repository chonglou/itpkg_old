package com.odong.itpkg.controller.admin;

import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.admin.CompressForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.util.DBHelper;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.SelectField;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午2:14
 */
@Controller("c.admin.db")
@RequestMapping(value = "/admin/database")
@SessionAttributes(SessionItem.KEY)
public class DatabaseController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getCompress(Map<String, Object> map) {

        map.put("lastCompress", siteService.getObject("site.lastCompress", Date.class));
        map.put("lastBackup", siteService.getObject("site.lastBackup", Date.class));
        map.put("dbSize", dbHelper.getSize());
        return "admin/database";
    }

    @RequestMapping(value = "/backup", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postBackup(@ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        try{
            dbHelper.backup();
            siteService.set("site.lastBackup", new Date());
            logService.add(si.getSsAccountId(), "备份数据库", Log.Type.INFO);
            ri.setOk(true);
        }
        catch (Exception e){
            ri.setOk(false);
            ri.addData(e.getMessage());
        }
        return ri;

    }

    @RequestMapping(value = "/compress", method = RequestMethod.GET)
    @ResponseBody
    Form getCompress() {
        Form fm = new Form("compress", "压缩数据", "/admin/database/compress");
        SelectField<Integer> daysKeep = new SelectField<>("days", "保留最近", 7);
        for (int i : new Integer[]{7, 30, 90, 180}) {
            daysKeep.addOption(i + "天", i);
        }
        fm.addField(daysKeep);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/compress", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postCompress(@Valid CompressForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (form.getDays() < 7) {
            ri.setOk(false);
            ri.addData("至少要保留最近一周的历史数据");
        }
        if (ri.isOk()) {
            dbHelper.compress(form.getDays());
            siteService.set("site.lastCompress", new Date());
            logService.add(si.getSsAccountId(), "压缩数据库，只保留最近[" + form.getDays() + "]天的数据", Log.Type.INFO);
        }
        return ri;

    }

    @Resource
    private DBHelper dbHelper;
    @Resource
    private LogService logService;
    @Resource
    private FormHelper formHelper;
    @Resource
    private SiteService siteService;

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }
}
