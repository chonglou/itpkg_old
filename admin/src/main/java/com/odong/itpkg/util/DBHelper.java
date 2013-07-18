package com.odong.itpkg.util;

import com.odong.itpkg.service.LogService;
import com.odong.itpkg.service.TaskService;
import com.odong.portal.config.Database;
import com.odong.portal.service.SiteService;
import com.odong.portal.util.ZipHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * mysqldump -u user -p database | gzip -9 > database.sql.gz
 * gunzip < database.sql.gz | mysql -u user -p database
 * <p/>
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-4
 * Time: 下午12:32
 */
@Component
public class DBHelper {
    public void compress(int keepDays){
        taskService.removeInvalid(keepDays);
        logService.removeOld(keepDays);
        siteService.set("db.last_compress", new Date());
    }
    public void backup() {
        if (database.isMysql()) {
            logger.info("开始备份数据库{}@mysql", database.getDbName());
            try {
                String fileName = appStoreDir + "/backup/" + database.getDbName() + "_" + format.format(new Date()) + ".sql";

                Process p = Runtime.getRuntime().exec("mysqldump -u " + database.getUsername()
                        + " -p" + database.getPassword() + " " + database.getDbName()
                        + " -r " + fileName);


                if (p.waitFor() == 0) {
                    logger.info("备份成功,开始压缩文件");
                    zipHelper.compress(fileName, true);
                } else {
                    logger.error("备份失败");
                }
            } catch (IOException | InterruptedException e) {
                logger.error("备份数据库{}@mysql出错", database.getDbName(), e);
            }
        }
    }

    @PostConstruct
    void init() {
        format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
    }

    @Resource
    private SiteService siteService;
    @Resource
    private TaskService taskService;
    @Resource
    private LogService logService;
    @Resource
    private ZipHelper zipHelper;
    @Resource
    private Database database;
    @Value("${app.store}")
    private String appStoreDir;
    private DateFormat format;

    private final static Logger logger = LoggerFactory.getLogger(DBHelper.class);

    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setZipHelper(ZipHelper zipHelper) {
        this.zipHelper = zipHelper;
    }

    public void setAppStoreDir(String appStoreDir) {
        this.appStoreDir = appStoreDir;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }



}
