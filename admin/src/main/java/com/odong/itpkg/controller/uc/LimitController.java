package com.odong.itpkg.controller.uc;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Mac;
import com.odong.itpkg.entity.net.firewall.DateLimit;
import com.odong.itpkg.entity.net.firewall.FlowLimit;
import com.odong.itpkg.entity.net.firewall.Output;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.form.net.limit.DateLimitForm;
import com.odong.itpkg.form.net.limit.FlowLimitForm;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.service.LogService;
import com.odong.portal.util.FormHelper;
import com.odong.portal.web.ResponseItem;
import com.odong.portal.web.form.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:29
 */
@Controller("c.uc.limit")
@RequestMapping(value = "/uc/limit")
@SessionAttributes(SessionItem.KEY)
public class LimitController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getLimit(Map<String, Object> map, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        map.put("dateLimitList", hostService.listFirewallDateLimit(si.getSsCompanyId()));
        map.put("flowLimitList", hostService.listFirewallFlowLimit(si.getSsCompanyId()));
        return "uc/limit";
    }

    @RequestMapping(value = "/date/{limit}", method = RequestMethod.GET)
    @ResponseBody
    Form getDateLimitForm(@PathVariable long limit, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        DateLimit dl = hostService.getFirewallDateLimit(limit);
        Form fm = new Form("limit", "修改时间规则[" + limit + "]", "/uc/limit/date");
        if (dl != null && dl.getCompany().equals(si.getSsCompanyId())) {
            fm.addField(new HiddenField<Long>("id", limit));
            fm.addField(new TextField<String>("name", "名称", dl.getName()));
            SelectField<String> begin = new SelectField<>("begin", "起始时间", time(dl.getBeginHour(), dl.getBeginMinute()));
            SelectField<String> end = new SelectField<>("end", "截止时间", time(dl.getEndHour(), dl.getEndMinute()));
            for (int i = 0; i < 24; i++) {
                for (int j = 0; j < 4; j++) {
                    String time = time(i, j * 15);
                    begin.addOption(time, time);
                    end.addOption(time, time);
                }
            }
            fm.addField(begin);
            fm.addField(end);
            Object[] week = new Object[]{
                    "mon", "一", dl.isMon(),
                    "tues", "二", dl.isTues(),
                    "wed", "三", dl.isWed(),
                    "thur", "四", dl.isThur(),
                    "fri", "五", dl.isFri(),
                    "sat", "六", dl.isSat(),
                    "sun", "日", dl.isSun()
            };


            for (int i = 0; i < week.length; i += 3) {
                RadioField<Boolean> rf = new RadioField<>((String) week[i], "星期" + (String) week[i + 1], (Boolean) week[i + 2]);
                rf.addOption("启动", true);
                rf.addOption("停止", false);
                fm.addField(rf);
            }


            fm.addField(new TextAreaField("details", "详情", dl.getDetails()));
            fm.setOk(true);
        } else {
            fm.addData("规则[" + limit + "]不存在");
        }
        return fm;
    }

    @RequestMapping(value = "/date/add", method = RequestMethod.GET)
    @ResponseBody
    Form getDateLimitForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {

        Form fm = new Form("limit", "新建时间规则", "/uc/limit/date");
        fm.addField(new HiddenField<Long>("id", null));
        fm.addField(new TextField<String>("name", "名称"));

        SelectField<String> begin = new SelectField<>("begin", "起始时间", "08:30");
        SelectField<String> end = new SelectField<>("end", "截止时间", "18:30");
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 4; j++) {
                String time = time(i, j * 15);
                begin.addOption(time, time);
                end.addOption(time, time);
            }
        }
        fm.addField(begin);
        fm.addField(end);
        Object[] week = new Object[]{
                "mon", "一", true,
                "thus", "二", true,
                "wed", "三", true,
                "thur", "四", true,
                "fri", "五", true,
                "sat", "六", false,
                "sun", "日", false
        };
        for (int i = 0; i < week.length; i += 3) {
            RadioField<Boolean> rf = new RadioField<>((String) week[i], "星期" + (String) week[i + 1], (Boolean) week[i + 2]);
            rf.addOption("启动", true);
            rf.addOption("停止", false);
            fm.addField(rf);
        }

        fm.addField(new TextAreaField("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/date", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postDateLimitAdd(@Valid DateLimitForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        String[] ssB = form.getBegin().split(":");
        String[] ssE = form.getEnd().split(":");
        int beginHour = Integer.parseInt(ssB[0]);
        int beginMinute = Integer.parseInt(ssB[1]);
        int endHour = Integer.parseInt(ssE[0]);
        int engMinute = Integer.parseInt(ssE[1]);
        if (ri.isOk()) {
            if (form.getId() == null) {
                hostService.addFirewallDateLimit(
                        si.getSsCompanyId(),
                        form.getName(), form.getDetails(),
                        beginHour, beginMinute, endHour, engMinute,
                        form.isMon(), form.isTues(), form.isWed(), form.isThur(), form.isFri(), form.isSat(), form.isSun()
                );
                logService.add(si.getSsAccountId(), "添加时间规则[" + form.getName() + "]", Log.Type.INFO);
            } else {
                DateLimit dl = hostService.getFirewallDateLimit(form.getId());
                if (dl != null && dl.getCompany().equals(si.getSsCompanyId())) {
                    hostService.setFirewallDateLimitInfo(form.getId(), form.getName(), form.getDetails());
                    hostService.setFirewallDateLimitTime(form.getId(), beginHour, beginMinute, endHour, engMinute);
                    hostService.setFirewallDateLimitWeekdays(form.getId(), form.isMon(), form.isTues(), form.isWed(), form.isThur(), form.isFri(), form.isSat(), form.isSun());
                } else {
                    ri.setOk(false);
                    ri.addData("时间规则[" + form.getId() + "]不存在");
                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/date/{limit}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem deleteDateLimit(@PathVariable long limit, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        DateLimit dl = hostService.getFirewallDateLimit(limit);
        if (dl != null && dl.getCompany().equals(si.getSsCompanyId())) {
            List<Output> outputs = hostService.listFirewallOutputByDateLimit(dl.getId());
            if (outputs.size() == 0) {
                hostService.delFirewallDateLimit(limit);
                logService.add(si.getSsAccountId(), "删除日期规则[" + limit + "]", Log.Type.INFO);
                ri.setOk(true);
            } else {
                ri.addData("日期规则[" + limit + "]正在使用，不能删除");
            }
        } else {
            ri.addData("日期规则[" + limit + "]不存在");
        }
        return ri;
    }

    @RequestMapping(value = "/flow/{limit}", method = RequestMethod.GET)
    @ResponseBody
    Form getFlowLimitForm(@PathVariable long limit, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        FlowLimit fl = hostService.getFirewallFlowLimit(limit);
        Form fm = new Form("limit", "修改流量规则[" + limit + "]", "/uc/limit/flow");
        if (fl != null && fl.getCompany().equals(si.getSsCompanyId())) {
            fm.addField(new HiddenField<Long>("id", limit));
            fm.addField(new TextField<>("name", "名称", fl.getName()));
            Object[] rules = new Object[]{
                    "downRate", "下行最小", fl.getDownRate(),
                    "downCeil", "下行最大", fl.getDownCeil(),
                    "upRate", "上行最小", fl.getUpRate(),
                    "upCeil", "上行最大", fl.getUpCeil()
            };

            for (int i = 0; i < rules.length; i += 3) {
                SelectField<Integer> sf = new SelectField<>((String) rules[i], (String) rules[i + 1], (Integer) rules[i + 2]);
                for (int j = 1; j < 10; j++) {
                    sf.addOption(String.format("%dK", j * 100), j * 100);
                }
                sf.addOption("1M", 1000);
                sf.addOption("1M", 1000);
                sf.addOption("1.5M", 1500);
                sf.addOption("2M", 2000);
                fm.addField(sf);
            }
            fm.addField(new TextAreaField("details", "详情", fl.getDetails()));
            fm.setOk(true);
        } else {
            fm.addData("流量规则[" + limit + "]不存在");
        }
        return fm;
    }

    @RequestMapping(value = "/flow/add", method = RequestMethod.GET)
    @ResponseBody
    Form getFlowLimitForm(@ModelAttribute(SessionItem.KEY) SessionItem si) {

        Form fm = new Form("limit", "新增流量规则", "/uc/limit/flow");

        fm.addField(new HiddenField<Long>("id", null));
        fm.addField(new TextField<>("name", "名称"));
        Object[] rules = new Object[]{
                "downRate", "下行最小", 100,
                "downCeil", "下行最大", 500,
                "upRate", "上行最小", 100,
                "upCeil", "上行最大", 200
        };

        for (int i = 0; i < rules.length; i += 3) {
            SelectField<Integer> sf = new SelectField<>((String) rules[i], (String) rules[i + 1], (Integer) rules[i + 2]);
            for (int j = 1; j < 10; j++) {
                sf.addOption(String.format("%dK", j * 100), j * 100);
            }
            sf.addOption("1M", 1000);
            sf.addOption("1.5M", 1500);
            sf.addOption("2M", 2000);
            fm.addField(sf);
        }
        fm.addField(new TextAreaField("details", "详情"));
        fm.setOk(true);
        return fm;
    }

    @RequestMapping(value = "/flow", method = RequestMethod.POST)
    @ResponseBody
    ResponseItem postFlowLimitAdd(@Valid FlowLimitForm form, BindingResult result, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = formHelper.check(result);
        if (form.getDownCeil() < form.getDownRate()) {
            ri.setOk(false);
            ri.addData("下行最大不能小于最小");
        }
        if (form.getUpCeil() < form.getUpRate()) {
            ri.setOk(false);
            ri.addData("上行最大不能小于最小");
        }
        if (ri.isOk()) {
            if (form.getId() == null) {
                hostService.addFirewallFlowLimit(si.getSsCompanyId(), form.getName(), form.getDetails(), form.getUpRate(), form.getUpCeil(), form.getDownRate(), form.getDownCeil());
                logService.add(si.getSsAccountId(), "添加限速规则[" + form.getName() + "]", Log.Type.INFO);
            } else {
                FlowLimit fl = hostService.getFirewallFlowLimit(form.getId());
                if (fl != null && fl.getCompany().equals(si.getSsCompanyId())) {
                    hostService.setFirewallFlowLimitInfo(form.getId(), form.getName(), form.getDetails());
                    hostService.setFirewallFlowLimitLine(form.getId(), form.getUpRate(), form.getUpCeil(), form.getDownRate(), form.getDownCeil());
                    logService.add(si.getSsAccountId(), "修改限速规则[" + form.getId() + "]", Log.Type.INFO);
                } else {
                    ri.setOk(false);
                    ri.addData("限速规则[" + form.getId() + "]不存在");
                }
            }
        }
        return ri;
    }

    @RequestMapping(value = "/flow/{limit}", method = RequestMethod.DELETE)
    @ResponseBody
    ResponseItem deleteFlowLimit(@PathVariable long limit, @ModelAttribute(SessionItem.KEY) SessionItem si) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        FlowLimit fl = hostService.getFirewallFlowLimit(limit);
        if (fl != null && fl.getCompany().equals(si.getSsCompanyId())) {
            List<Mac> macList = hostService.listMacByFirewallFlowLimit(fl.getId());
            List<Host> hostList = hostService.listHostByFlowLimit(fl.getId());
            if (macList.size() == 0 && hostList.size()==0) {
                hostService.delFirewallFlowLimit(limit);
                logService.add(si.getSsAccountId(), "删除流量规则[" + limit + "]", Log.Type.INFO);
                ri.setOk(true);
            } else {
                ri.addData("流量规则[" + limit + "]正在使用，不能删除");
            }

        } else {
            ri.addData("流量规则[" + limit + "]不存在");
        }
        return ri;
    }

    private String time(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    @Resource
    private HostService hostService;
    @Resource
    private LogService logService;
    @Resource
    private FormHelper formHelper;
    private final static Logger logger = LoggerFactory.getLogger(LimitController.class);

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setFormHelper(FormHelper formHelper) {
        this.formHelper = formHelper;
    }

}
