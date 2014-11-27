IT包
=====

### 编译环境
    go get code.google.com/p/go.net/websocket
    go install code.google.com/p/go.net/websocket
    go install github.com/ActiveState/tail

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
 * 全程ssl加密链路
 * XMPP协议通信


### 笔记

#### 测试
    rspec

#### 创建项目

    rails new itpkg --database=mysql

#### 创建Model

    rails generate model name

#### 创建controller

    rails generate controller name act1 act2  --no-assets --no-helper

#### 创建集成测试

    rails generate integration_test act

#### 启动

    rails server

#### 控制台

    rails console

#### 制作补丁
    diff -uN /var/abs/extra/bind/PKGBUILD PKGBUILD > bind-mysql.patch
    patch -p0 < bind-mysql.patch

#### 数据库操作

    rails dbconsole

#### ubuntu 无提示安装mysql

    sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password password YOUR_PASSWORD'
    sudo debconf-set-selections <<< 'mysql-server mysql-server/root_password_again password YOUR_PASSWORD'
    sudo apt-get -y install mysql-server


#### 清空mysql root密码
    UPDATE mysql.user SET Password=PASSWORD('') WHERE User='root';
    FLUSH PRIVILEGES; 

### redis常用命令
    FLUSHDB # 清除当前数据库
    FLUSHALL # 清除所有数据
    SELECT 1 # 使用数据库1
    QUIT # 退出

### sendmail设置

    sudo pacman -Syu msmtp msmtp-mta
    vi ~/.msmtprc
    chmod 600 ~/.msmtprc



