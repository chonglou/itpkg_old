package com.odong.itpkg.controller.personal;

import com.odong.itpkg.entity.uc.Account;
import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.entity.uc.Log;
import com.odong.itpkg.model.Contact;
import com.odong.itpkg.model.SessionItem;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.LogService;
import com.odong.itpkg.service.RbacService;
import com.odong.portal.util.TimeHelper;
import com.odong.portal.web.ResponseItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午1:03
 */
@Controller("c.personal.valid")
@RequestMapping(value = "/personal")
@SessionAttributes(SessionItem.KEY)
public class ValidController extends EmailController {

    @RequestMapping(value = "/valid", method = RequestMethod.GET)
    String getValidCode(HttpServletRequest request, Map<String, Object> map) {

        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        Map<String, String> mapA = jsonHelper.json2map(encryptHelper.decode(request.getParameter("code")), String.class, String.class);
        String email = mapA.get("email");
        String type = mapA.get("type");
        String created = mapA.get("created");

        if (email == null || type == null || created == null) {
            ri.addData("链接信息不全");
        } else if (timeHelper.plus(new Date(Long.parseLong(created)), 60 * linkValid).compareTo(new Date()) < 0
                ) {
            ri.addData("链接失效，请重新申请。");
        } else {
            Account u = accountService.getAccount(email);
            switch (Type.valueOf(type)) {
                case REGISTER:
                    if (u != null && u.getState() == Account.State.SUBMIT) {
                        Company c = accountService.getCompany(u.getCompany());

                        switch (c.getState()) {
                            case SUBMIT:
                                accountService.setCompanyState(u.getCompany(), Company.State.ENABLE);
                                logService.add(u.getId(), "公司激活", Log.Type.INFO);
                                activeAccount(u.getId(), u.getEmail());
                                rbacService.bindCompany(u.getId(), u.getCompany(), RbacService.OperationType.MANAGE, true);
                                logService.add(u.getId(), "授予自己公司管理员权限", Log.Type.INFO);
                                accountService.addUser("默认用户", "默认部门", new Contact(), u.getCompany());
                                logService.add(u.getId(), "添加默认用户", Log.Type.INFO);
                                ri.setOk(true);
                                ri.addData("您成功激活了公司和用户");
                                break;
                            case ENABLE:
                                activeAccount(u.getId(), u.getEmail());
                                rbacService.bindCompany(u.getId(), u.getCompany(), RbacService.OperationType.USE, true);
                                logService.add(u.getId(), "授予公司用户权限", Log.Type.INFO);
                                ri.setOk(true);
                                ri.addData("您成功激活了账户");
                                break;
                            default:
                                ri.addData("公司[" + c.getName() + "]状态不对");
                                break;
                        }


                    } else {
                        ri.addData("账户[" + email + "]状态不对");
                    }
                    break;
                case RESET_PWD:
                    if (u.getState() == Account.State.ENABLE) {
                        if (new Date().compareTo(timeHelper.plus(jsonHelper.json2object(mapA.get("created"), Date.class), 60 * 30)) <= 0) {
                            accountService.setAccountPassword(u.getId(), mapA.get("password"));
                            logService.add(u.getId(), "重置密码", Log.Type.INFO);
                            emailHelper.send(email, "您在[" + siteService.getString("site.domain") + "]上的成功重置了密码",
                                    "如果不是您的操作，请忽略该邮件。", true);
                            ri.setOk(true);
                            ri.addData("您成功重置了密码");
                        } else {
                            ri.addData("链接已失效");
                        }
                    } else {
                        ri.addData("用户[" + u.getEmail() + "]状态不对");
                    }
                    break;
                default:
                    ri.addData("未知的操作");
                    break;
            }
        }
        map.put("item", ri);
        return "message";
    }


    private void activeAccount(long userId, String email) {
        accountService.setAccountState(userId, Account.State.ENABLE);
        logService.add(userId, "账户激活", Log.Type.INFO);
        emailHelper.send(
                email,
                "您在[" + siteService.getString("site.title") + "]上的激活了账户",
                "欢迎使用",
                true);
    }

    @Resource
    private AccountService accountService;
    @Resource
    private TimeHelper timeHelper;
    @Resource
    private LogService logService;
    @Resource
    private RbacService rbacService;
}
