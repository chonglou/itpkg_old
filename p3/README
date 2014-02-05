配置文件在：web.cfg
app_secret需要16位

默认数据存储在tmp目录下

密钥可以使用"pwgen -n 16"生成

需要中文字体文件在tmp/font.ttc(验证码需要)

启动：
brahma.sh start

停止：
brahma.sh stop

调试：
brahma.sh debug


加载python环境：
mkdir tmp
cd tmp
virtualenv python3
cd ..
source tmp/python3/bin/activate
pacman -S tcl tk
pip install Pillow
pip install tornado pycurl pycrypto redis WTForms wtforms-tornado beaker qrcode markdown psutil pexpect
pip install --allow-all-external mysql-connector-python

退出python环境：
deactivate

ueditor编辑器设定：禁止全屏

查看进程git：ps axjf | grep python
