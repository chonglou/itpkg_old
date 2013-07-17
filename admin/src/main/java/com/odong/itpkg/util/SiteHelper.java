package com.odong.itpkg.util;

import com.odong.itpkg.Constants;
import com.odong.itpkg.entity.uc.User;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.RbacService;
import com.odong.portal.service.SiteService;
import httl.spi.resolvers.GlobalResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-22
 * Time: 下午11:41
 */
@Component("siteHelper")
public class SiteHelper {
    @PostConstruct
    void init() {
        siteService.set("site.startup", new Date());
        if (siteService.getObject("site.init", Date.class) == null) {
            siteService.set("site.init", new Date());
            siteService.set("site.version", Constants.VERSION);
            siteService.set("site.title", "ITPKG-企业信息化管理系统");
            siteService.set("site.description", "itpkg");
            siteService.set("site.keywords", "itpkg");
            siteService.set("site.domain", "www.0-dong.com");
            siteService.set("site.copyright", "&copy;2013");
            siteService.set("site.allowRegister", true);
            siteService.set("site.allowLogin", true);
            siteService.set("site.aboutMe", "关于我们");
            siteService.set("site.regProtocol", "注册协议");
            siteService.set("site.author", "zhengjitang@gmail.com");

            String email = "flamen@0-dong.com";
            String company = accountService.addCompany("IT-PACKAGE", "管理员");
            accountService.addUser(email, "管理员", "123456", company);
            User admin = accountService.getUser(email);
            rbacService.bindAdmin(admin.getId(), true);
        }

        GlobalResolver.put("gl_debug", appDebug);

        File base = new File(appStoreDir);
        if (base.exists()) {
            if (!base.isDirectory() || !base.canWrite()) {
                throw new RuntimeException("数据存储目录[" + appStoreDir + "]不可用");
            }
        }
        if (!base.exists()) {
            logger.info("数据存储目录[{}]不存在,创建之!", appStoreDir);
            for (String s : new String[]{"backup", "seo", "attach"}) {
                String dir = appStoreDir + "/" + s;
                File f = new File(dir);
                if (f.mkdirs()) {
                    logger.info("创建数据目录[{}]成功", dir);
                } else {
                    throw new IllegalArgumentException("数据存储目录[" + dir + "]创建失败");
                }
            }
        }
    }

    @PreDestroy
    void destroy() {
        siteService.set("site.shutdown", new Date());
    }

    @Resource
    private RbacService rbacService;
    @Resource
    private AccountService accountService;
    @Resource
    private StringHelper stringHelper;
    @Resource
    private SiteService siteService;
    @Value("${app.store}")
    private String appStoreDir;
    @Value("${app.debug}")
    private boolean appDebug;
    private final static Logger logger = LoggerFactory.getLogger(SiteHelper.class);

    public void setRbacService(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setAppDebug(boolean appDebug) {
        this.appDebug = appDebug;
    }

    public void setAppStoreDir(String appStoreDir) {
        this.appStoreDir = appStoreDir;
    }

    public void setStringHelper(StringHelper stringHelper) {
        this.stringHelper = stringHelper;
    }

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }
}
