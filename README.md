IT-PACKAGE 自动化运维系统
==========

#### 管理端
    rake brahma:web:start # 启动
    
#### 后台任务
    rake brahma:dispatcher:start # 分发程序 
    rake brahma:worker:start[id] # 启动工作者进程 id为整数
    rake brahma:listener:start[id] # 启动监听进程 id为整数

#### 代理端
    rake brahma:agent # 编译
    ./itpkg-agent # 启动

