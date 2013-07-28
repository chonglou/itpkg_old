package com.odong.portal.util;

import com.google.code.kaptcha.Constants;
import com.odong.portal.service.SiteService;
import com.odong.portal.web.ResponseItem;
import net.tanesha.recaptcha.ReCaptchaResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-23
 * Time: 上午12:02
 */
@Component("formHelper")
public class FormHelper {
    public boolean checkIp(String ip) {
        if (ip == null) {
            return false;
        }
        String[] ss = ip.split("\\.");
        if (ss.length != 4) {
            return false;
        }

        try {
            for (String s : ss) {
                int i = Integer.parseInt(s);
                if (i < 0 && i > 255) {
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public ResponseItem check(BindingResult result) {
        return check(result, null, false);
    }

    public boolean checkKaptcha(HttpServletRequest request) {

        String captchaS = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        String captchaR = request.getParameter("captcha");
        return StringUtils.equals(captchaS, captchaR);
    }

    public boolean checkReCaptcha(ResponseItem item, HttpServletRequest request) {
        try {
            ReCaptchaResponse response = captchaHelper.getReCaptcha().checkAnswer(
                    request.getRemoteAddr(),
                    request.getParameter("challenge"),
                    request.getParameter("captcha")
            );
            if (response.isValid()) {
                return true;
            }
            item.addData(response.getErrorMessage());
        } catch (NullPointerException e) {
            item.addData("未找到验证码");
        }

        return false;
    }

    public ResponseItem check(BindingResult result, HttpServletRequest request, boolean captcha) {
        ResponseItem ri = new ResponseItem(ResponseItem.Type.message);
        if (captcha) {
            switch (siteService.getString("site.captcha")) {
                case "kaptcha":
                    if (!checkKaptcha(request)) {
                        ri.addData("验证码输入不正确");
                    }
                    break;
                case "reCaptcha":
                    if (!checkReCaptcha(ri, request)) {
                        ri.addData("验证码输入不正确");
                    }
                    break;
                default:
                    ri.addData("未知的验证码引擎");

                    break;
            }
        }


        for (ObjectError error : result.getAllErrors()) {
            ri.addData(error.getDefaultMessage());
        }
        if (ri.getData().size() == 0) {
            ri.setOk(true);
        }
        return ri;
    }


    @Resource
    private CaptchaHelper captchaHelper;
    @Resource
    private SiteService siteService;

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setCaptchaHelper(CaptchaHelper captchaHelper) {
        this.captchaHelper = captchaHelper;
    }
}
