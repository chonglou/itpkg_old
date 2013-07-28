package com.odong.itpkg.util;

import com.odong.itpkg.entity.Task;
import com.odong.itpkg.entity.uc.Account;
import com.odong.itpkg.entity.uc.Company;
import com.odong.itpkg.model.KaptchaProfile;
import com.odong.itpkg.model.ReCaptchaProfile;
import com.odong.itpkg.model.SmtpProfile;
import com.odong.itpkg.service.AccountService;
import com.odong.itpkg.service.RbacService;
import com.odong.itpkg.service.TaskService;
import com.odong.portal.config.Database;
import com.odong.portal.email.EmailHelper;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.TimeHelper;
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
            siteService.set("site.version", "v20130716");
            siteService.set("site.title", "IT-PACKAGE(企业信息化管理系统)");
            siteService.set("site.description", "itpkg");
            siteService.set("site.keywords", "itpkg");
            siteService.set("site.domain", "www.0-dong.com");
            siteService.set("site.copyright", "&copy;2013");
            siteService.set("site.allowRegister", true);
            siteService.set("site.allowLogin", true);
            siteService.set("site.aboutMe", "关于我们");
            siteService.set("site.regProtocol", "注册协议");
            siteService.set("site.author", "zhengjitang@gmail.com");

            //SMTP
            SmtpProfile smtp = new SmtpProfile();
            smtp.setPort(25);
            emailHelper.setup(smtp);
            emailHelper.reload();

            //KAPTCHA
            KaptchaProfile kaptcha = new KaptchaProfile();
            kaptcha.setLength(4);
            kaptcha.setChars("0123456789");
            kaptcha.setHeight(56);
            kaptcha.setWidth(100);
            siteService.set("site.kaptcha", kaptcha);

            //RECAPTCHA
            ReCaptchaProfile reCaptcha = new ReCaptchaProfile();
            siteService.set("site.reCaptcha", reCaptcha);
            siteService.set("site.captcha", "kaptcha");


            String email = "flamen@0-dong.com";
            String companyId = "admin";
            accountService.addCompany(companyId, "IT-PACKAGE", "");
            accountService.setCompanyState(companyId, Company.State.ENABLE);
            accountService.addAccount(companyId, email, "管理员", "123456");
            Account admin = accountService.getAccount(email);
            accountService.setAccountState(admin.getId(), Account.State.ENABLE);
            rbacService.bindAdmin(admin.getId(), true);
            rbacService.bindCompany(admin.getId(), companyId, RbacService.OperationType.MANAGE, true);

            taskService.add(Task.Type.SYS_GC, null, timeHelper.nextDay(gcHour), timeHelper.max(), 0, 60 * 60 * 24);
            if (database.isMysql()) {
                taskService.add(Task.Type.DB_BACKUP, null, timeHelper.nextDay(backupHour), timeHelper.max(), 0, 60 * 60 * 24);
            }
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
            for (String s : new String[]{"backup", "attach"}) {
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
    private EmailHelper emailHelper;
    @Resource
    private Database database;
    @Resource
    private TimeHelper timeHelper;
    @Resource
    private TaskService taskService;
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
    @Value("${backup.hour}")
    private int backupHour;
    @Value("${gc.hour}")
    private int gcHour;
    private final static Logger logger = LoggerFactory.getLogger(SiteHelper.class);

    public void setEmailHelper(EmailHelper emailHelper) {
        this.emailHelper = emailHelper;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setBackupHour(int backupHour) {
        this.backupHour = backupHour;
    }

    public void setGcHour(int gcHour) {
        this.gcHour = gcHour;
    }

    public void setTimeHelper(TimeHelper timeHelper) {
        this.timeHelper = timeHelper;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

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
