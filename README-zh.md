IT包
=====

### 配置信息
 * ITPKG_DATABASE_PASSWORD mysql密码
 * ITPKG_SECRET_KEY_BASE 128位
 * ITPKG_DOMAIN 域名
 * ITPKG_MEMCACHED_HOSTS memcached主机列表，逗号分割
 * ITPKG_DEVISE_SECRET_KEY 128位
 * ITPKG_MAILER_SENDER 邮件用户名

### 功能列表

 * 项目管理
 * 代码仓库管理(git)
 * 知识库管理
 * 监控系统(snmp mysql nginx)
 * 日志分析
 * email系统管理
 * vpn 系统管理
 * 邮件处理
 * 简单帐目管理

### 笔记

 * 创建项目

    rails new itpkg --database=mysql

 * 创建Model

    rails generate model name

 * 创建controller

   rails generate controller name act1 act2  --no-assets --no-helper

 * 启动

    rails server

 * 控制台

    rails console

 * 数据库操作

    rails dbconsole


 * 清空mysql root密码
    UPDATE mysql.user SET Password=PASSWORD('') WHERE User='root';
    FLUSH PRIVILEGES; 




