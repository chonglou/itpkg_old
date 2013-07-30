package com.odong.portal.controller;


import com.google.code.kaptcha.Constants;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.CaptchaHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午11:30
 */

@Controller("captcha")
public class CaptchaController {
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        switch (siteService.getString("site.captcha")) {
            case "reCaptcha":
                buildReCaptcha(request, response);
                break;
            case "kaptcha":
                buildKaptcha(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }

    private void buildReCaptcha(HttpServletRequest request, HttpServletResponse response) {

        try (
                ServletOutputStream out = response.getOutputStream()
        ) {
            out.print(captchaHelper.getReCaptcha().createRecaptchaHtml(request.getParameter("error"), null));
            out.flush();
        } catch (IOException e) {
            logger.error("生成验证码出错", e);
        }
    }


    private void buildKaptcha(HttpServletRequest request, HttpServletResponse response) {
        // Set to expire far in the past.
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");

        // return a jpeg
        response.setContentType("image/jpeg");

        // create the text for the image
        String capText = captchaHelper.getKaptcha().createText();

        // store the text in the session
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);

        // create the image with the text
        BufferedImage bi = captchaHelper.getKaptcha().createImage(capText);

        try {
            ServletOutputStream out = response.getOutputStream();

            // write the data out
            ImageIO.write(bi, "jpg", out);
            try {
                out.flush();
            } finally {
                out.close();
            }
        } catch (IOException e) {
            logger.error("生成验证码出错", e);
        }

    }

    @Resource
    private SiteService siteService;
    @Resource
    private CaptchaHelper captchaHelper;
    private final static Logger logger = LoggerFactory.getLogger(CaptchaController.class);

    public void setCaptchaHelper(CaptchaHelper captchaHelper) {
        this.captchaHelper = captchaHelper;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

}
