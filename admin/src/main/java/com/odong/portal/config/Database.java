package com.odong.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-6-4
 * Time: 下午1:18
 */
@Component("config.database")
public class Database {

    @PostConstruct
    void init() throws ClassNotFoundException, SQLException {
        dbName = url.split("/")[3];
        if (isMysql()) {
            logger.info("使用mysql jdbc驱动，如果数据库[{}]不存在，将会自动创建", dbName);
            Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url.substring(0, url.lastIndexOf('/')), username, password);
                 Statement stat = conn.createStatement()) {
                stat.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName + " CHARACTER SET  utf8");
            }
            return;
        }
        logger.warn("尚不支持[{}]自动创建,请自行确保数据库[{}]存在", driver, dbName);

    }

    public boolean isMysql() {
        return "com.mysql.jdbc.Driver".equals(driver);
    }


    @Value("${jdbc.driver}")
    private String driver;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;
    private String dbName;
    private final static Logger logger = LoggerFactory.getLogger(Database.class);

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getDbName() {
        return dbName;
    }

    public String getPassword() {
        return password;
    }
}
