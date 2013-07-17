package com.odong.portal.config;

import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.instrument.classloading.LoadTimeWeaver;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-5-23
 * Time: 下午8:27
 */
@Configuration("config.jpa")
@EnableTransactionManagement
public class Jpa {
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {

        Properties props = new Properties();
        props.setProperty("hibernate.dialect", hibernateDialect);
        props.setProperty("hibernate.show_sql", Boolean.toString(hibernateShowSql));
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);

        props.setProperty("hibernate.query.substitutions", "true 1, false 0");
        props.setProperty("hibernate.default_batch_fetch_size", Integer.toString(hibernateDefaultBatchFetchSize));
        props.setProperty("hibernate.max_fetch_depth", Integer.toString(hibernateMaxFetchDepth));
        props.setProperty("hibernate.generate_statistics", "true");
        props.setProperty("hibernate.bytecode.use_reflection_optimizer", "true");

        props.setProperty("hibernate.cache.use_second_level_cache", "true");
        props.setProperty("hibernate.cache.use_query_cache", "true");
        props.setProperty("hibernate.cache.region.factory_class", "org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory");
        props.setProperty("hibernate.ejb.naming_strategy", "com.odong.portal.config.NamingStrategy");


        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(getDataSource());
        factoryBean.setPackagesToScan("com.odong.**.entity");


        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setJpaProperties(props);
        factoryBean.setLoadTimeWeaver(getLoadTimeWeaver());
        factoryBean.setJpaDialect(getJpaDialect());

        return factoryBean;
    }


    @Bean
    JpaDialect getJpaDialect() {
        return new HibernateJpaDialect();
    }


    @Bean
    LoadTimeWeaver getLoadTimeWeaver() {
        return new InstrumentationLoadTimeWeaver();
    }


    @Bean(destroyMethod = "close")
    @DependsOn("database.init")
    BoneCPDataSource getDataSource() {
        BoneCPDataSource ds = new BoneCPDataSource();
        ds.setDriverClass(jdbcDriver);
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(jdbcUsername);
        ds.setPassword(jdbcPassword);
        ds.setIdleConnectionTestPeriodInSeconds(60);
        ds.setIdleMaxAgeInSeconds(300);
        ds.setMaxConnectionsPerPartition(poolMaxConnectionsPerPartition);
        ds.setMinConnectionsPerPartition(poolMinConnectionsPerPartition);
        ds.setPartitionCount(poolPartitionCount);
        ds.setAcquireIncrement(5);
        ds.setStatementsCacheSize(poolStatementsCacheSize);
        ds.setReleaseHelperThreads(3);
        return ds;
    }


    @Bean(name = "db.txManager")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
                entityManagerFactoryBean().getObject());

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Value("${jdbc.driver}")
    private String jdbcDriver;
    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.username}")
    private String jdbcUsername;
    @Value("${jdbc.password}")
    private String jdbcPassword;

    @Value("${hibernate.dialect}")
    private String hibernateDialect;
    @Value("${hibernate.hbm2ddl.auto}")
    private String hibernateHbm2ddlAuto;
    @Value("${hibernate.show_sql}")
    private boolean hibernateShowSql;
    @Value("${hibernate.default_batch_fetch_size}")
    private int hibernateDefaultBatchFetchSize;
    @Value("${hibernate.max_fetch_depth}")
    private int hibernateMaxFetchDepth;

    @Value("${pool.max_connections_per_partition}")
    private int poolMaxConnectionsPerPartition;
    @Value("${pool.min_connections_per_partition}")
    private int poolMinConnectionsPerPartition;
    @Value("${pool.partition_count}")
    private int poolPartitionCount;
    @Value("${pool.statements_cache_size}")
    private int poolStatementsCacheSize;

    @Resource
    private JpaVendorAdapter jpaVendorAdapter;

    public void setJpaVendorAdapter(JpaVendorAdapter jpaVendorAdapter) {
        this.jpaVendorAdapter = jpaVendorAdapter;
    }


    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }

    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    public void setHibernateDialect(String hibernateDialect) {
        this.hibernateDialect = hibernateDialect;
    }

    public void setHibernateHbm2ddlAuto(String hibernateHbm2ddlAuto) {
        this.hibernateHbm2ddlAuto = hibernateHbm2ddlAuto;
    }

    public void setHibernateShowSql(boolean hibernateShowSql) {
        this.hibernateShowSql = hibernateShowSql;
    }

    public void setHibernateDefaultBatchFetchSize(int hibernateDefaultBatchFetchSize) {
        this.hibernateDefaultBatchFetchSize = hibernateDefaultBatchFetchSize;
    }

    public void setHibernateMaxFetchDepth(int hibernateMaxFetchDepth) {
        this.hibernateMaxFetchDepth = hibernateMaxFetchDepth;
    }

    public void setPoolMaxConnectionsPerPartition(int poolMaxConnectionsPerPartition) {
        this.poolMaxConnectionsPerPartition = poolMaxConnectionsPerPartition;
    }

    public void setPoolMinConnectionsPerPartition(int poolMinConnectionsPerPartition) {
        this.poolMinConnectionsPerPartition = poolMinConnectionsPerPartition;
    }

    public void setPoolPartitionCount(int poolPartitionCount) {
        this.poolPartitionCount = poolPartitionCount;
    }

    public void setPoolStatementsCacheSize(int poolStatementsCacheSize) {
        this.poolStatementsCacheSize = poolStatementsCacheSize;
    }
}
