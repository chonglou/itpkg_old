package com.odong.itpkg.controller.admin;

import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.admin.CompressForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.util.DBHelper;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.Form;
import com.odong.portal.web.form.SelectField;
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
 * Time: 下午2:14
 */
@Controller("c.admin.compress")
@RequestMapping(value = "/admin/compress")
@SessionAttributes(SessionItem.KEY)
public class CompressController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getCompress(Map<String, Object> map) {

        map.put("dbSize", dbHelper.getSize());
        return "admin/compress";
    }

    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    Form getCompressFM() {
        Form fm = new Form("compress", "压缩数据", "/admin/compress/info");
        SelectField<Integer> daysKeep = new SelectField<>("days", "保留最近", 7);
        for (int i : new Integer[]{7, 30, 90, 180}) {
            daysKeep.addOption(i + "天", i);
        }
        fm.addField(daysKeep);
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postCompressFM(@Valid CompressForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (form.getDays() < 7) {
            ri.setOk(false);
            ri.addData("至少要保留最近一周的历史数据");
        }
        if (ri.isOk()) {
            dbHelper.compress(form.getDays());
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
