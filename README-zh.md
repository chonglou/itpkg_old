IT包
=====

### 配置信息
 * ITPKG_DATABASE_PASSWORD mysql密码
 * SECRET_KEY_BASE 128位
 * ITPKG_DOMAIN 域名
 * ITPKG_MEMCACHED_HOSTS memcached主机列表，逗号分割

### 功能列表

 * 项目管理
 * 代码仓库管理(git)
 * 知识库管理
 * 监控系统(snmp mysql nginx)
 * 日志分析(rsyslog)

### 笔记

 * 创建项目

    rails new itpkg --database=mysql

 * 创建Model

    rails generate model name

 * 创建controller

   rails generate controller name act1 act2  --no-assets --no-helper


 * 清空mysql root密码
    UPDATE mysql.user SET Password=PASSWORD('') WHERE User='root';
    FLUSH PRIVILEGES; 




