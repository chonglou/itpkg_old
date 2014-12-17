IT-PACKAGE 开发文档
-----------------------

IT-PACKAGE(缩写：itpkg)是一款定位于企业运维自动化的解决方案包，提供项目管理，任务管理（番茄工作法），邮件处理，在线沟通（XMPP）,
文档管理，GIT仓库管理，DOCKER仓库管理，日志集中存储及分析，服务器状态监控及阈值报警，简单帐目管理等常见运维功能。

可以认为是专注于ops的oa系统

由于当前处于快速开发阶段，在v1.0 release发布之前，暂不保证向前兼容性，如需升级，请联系开发人员进行技术指导。

Itpkg默认通信链路通过vpn进行保护


## 代码结构

### 部署架构图
![Deploy Arch](https://raw.githubusercontent.com/chonglou/itpkg/master/doc/pictures/arch.png)
### 分支说明

 * master 主分支 用于发布
 * development 主开发分支 用于开发及测试
 * python2 早期python2版本 只含局域网管理功能，后用python3+tronado重写
 * 其余 废弃分支 会在适当时候移除

除了master和development分支外 其余可以自动忽略

### 目录说明
标准的 rails目录，其余的
#### doc目录
按语言排列 里面为帮助文档
#### tools目录

 * agent 为代理端代码 用ruby来验证功能，具体部署时用go
 * docker 为镜像制作脚本
 * seeds 默认模板，运行rake db:seed会自动导入数据库


## 开发指导

默认用户: root changme

### 开发环境
强烈建议使用archlinux or mac

#### ArchLinux

    sudo pacman -S  git base-devel cmake libmariadbclient libgit2 percona-server percona-server-clients nodejs redis mongodb elasticsearch
    sudo systemctl start mysqld 
    sudo systemctl start redis 
    sudo systemctl start mongodb
    sudo systemctl start elasticsearch

#### Mac

    #安装brew
    ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
    brew install git 



#### rbenv
    git clone https://github.com/sstephenson/rbenv.git ~/.rbenv
    git clone https://github.com/sstephenson/ruby-build.git ~/.rbenv/plugins/ruby-build
    git clone https://github.com/sstephenson/rbenv-vars.git ~/.rbenv/plugins/ruby-vars
    echo 'export PATH="$HOME/.rbenv/bin:$PATH"' >> ~/.bashrc
    echo 'eval "$(rbenv init -)"' >> ~/.bashrc
    # 重新登录 使.bashrc生效
    rbenv install 2.1.5
    git clone git@github.com:chonglou/itpkg.git
    cd itpkg
    gem install bundler # 如果发生错误 一般是缺失相应的lib文件 补上即可
    bundle install
    rbenv rehash

数据库及redis设置在.rbenv文件

如果需要更新rbenv

    cd ~/.rbenv
    git pull
    cd plugins/ruby-build
    git pull
    cd ../ruby-vars
    git pull

#### 启动
    rails server
    firefox http://www.localhost.localdomain:3000
    firefox http://mail.localhost.localdomain:3000



### 代码风格

六个字：干净 优雅 舒服

#### 关于测试（先上车 后补票）

 * 初期由于赶开发进度 测试程序可以等功能测试稳定发布前补上
 * 一般只做功能测试(rspec+capybara+factory_girl+selenium) 覆盖率要尽可能高
 * 其余的根据需要，不做强制要求

#### 关于controller

 * 避免不必要的 helper和test 文件


    rails generate controller NAME ACTION --no-test-framework --no-assets --no-helper


 * generate/destroy之后 要检查routes.rb文件 放my add行之后


#### 关于model (仅适用于v1.0 release发布前)

 * model说明
基本所有的details都用markdown语法


    bg_log # 后台任务日志
    cdn/* # cdn服务器相关
    certificate # ssl证书管理
    confirmation # 确认链接信息
    contact # 联系人信息
    dns/* # dns服务器相关
    document # 文档文件
    email/* # email服务器相关
    finance # 简单的出入帐 有待扩展
    log # 操作日志
    mail_box* # 邮件处理
    node_*和nt_* # 集群节点管理
    notice # 站内通知公告
    permission # rbac权限表
    project_* story s_* # 项目/任务管理
    repository_* # git 仓库管理
    rss_site # rss站点 用于团队知识库
    setting # 站点参数
    ssh_key # ssh key pairs
    user # 用户表
    vpn/* # vpn服务器管理
    wiki* # 知识库管理


 

#### 关于view
 * 优先考虑使用slim
 * form优先考虑使用bootstrap_form



#### 关于图片

 * 初期上[http://www.flaticon.com/most-downloaded/](http://www.flaticon.com/most-downloaded/)凑合
 * 格式用png,大小用256x256或其它标准尺寸，注意用file检查文件大小是否相符
 * 放vendor/assets/images/flat/256目录下, 其它大小类推

这几条原则只是为了方便后期美工跟进

### 分支
以f_或b_开头是为了方便做持续集成测试时进行区分

#### 新增feature
命名规则： f_feature_name

 * 来自于development分支
 * 测试通过之后 merge回development


#### bug修复
命名规则： b_feature_name
 * 来自于master分支
 * 测试通过后 merge回development


### 发布

#### 代码
每周六 development分支测试通过之后 merge至master分支

#### 镜像
每月最后一个周六 重新打包镜像并上传


## 加入我们
 * github: [https://github.com/chonglou/itpkg](https://github.com/chonglou/itpkg)
 * pivotaltracker: [https://www.pivotaltracker.com/n/projects/1153792](https://www.pivotaltracker.com/n/projects/1153792)

