package com.odong.itpkg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 下午2:44
 */
@Component("bootCheck")
public class BootCheck {

    @PostConstruct
    void init() {
        if ("root".equals(System.getProperty("user.name"))) {
            udev();
            wan();
        } else {
            logger.warn("调试启动");
        }
    }

    private void wan() {
        String wanF = "/etc/netctl/wan";
        if (FileHelper.exist(wanF)) {
            logger.info("IP地址配置检查通过，如果变更了公网IP，请先停止服务，然后删除{}文件", wanF);
        } else {
            String wanM = "rw-r--r--";
            if ("dhcp".equals(wan)) {
                FileHelper.write(wanF, wanM,
                        "Description='A dhcp ethernet connection for wan'",
                        "Interface=wan",
                        "Connection=ethernet",
                        "IP=dhcp"
                );
            } else {
                String[] ss = wan.split(" ");
                if (ss.length == 5) {
                    FileHelper.write(wanF, wanM,
                            "Description='A static ethernet connection for wan'",
                            "Interface=wan",
                            "Connection=ethernet",
                            "IP=static",
                            "Address=('" + ss[0] + "/24')",
                            "Gateway='" + ss[2] + "'",
                            "DNS=('" + ss[3] + " " + ss[4] + "')"
                    );
                } else {
                    logger.error("IP配置信息不正确[{}]", wan);
                }
            }

            CommandHelper.execute("netctl enable wan");
            CommandHelper.execute("netctl start wan");
            logger.info("成功配置IP地址");
        }
    }

    private void reboot() {
        logger.info("网卡配置成功，5秒钟后自动重启主机");
        try {
            Thread.sleep(1000 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CommandHelper.execute("reboot");
    }

    private void udev() {
        String udev = "/etc/udev/rules.d/10-network.rules";
        if (FileHelper.exist(udev)) {
            logger.info("网卡配置检查通过，如果更新网卡，请先停止服务，然后删除文件{}", udev);
        } else {
            logger.info("未发现网卡配置文件，正在启动配置");
            List<String> macs = CommandHelper.execute("ifconfig -a| grep ether | awk -F\" \" '{print $2}'");
            if (macs.size() == 2) {
                FileHelper.write(udev, "rw-r--r--",
                        "SUBSYSTEM==\"net\", ACTION==\"add\", ATTR{address}==\"" + macs.get(0) + "\", NAME=\"wan\"",
                        "SUBSYSTEM==\"net\", ACTION==\"add\", ATTR{address}==\"" + macs.get(1) + "\", NAME=\"lan\""
                );
                logger.info("WAN：{}", macs.get(0));
                logger.info("LAN：{}", macs.get(1));
            } else if (macs.size() == 3) {
                FileHelper.write(udev, "rw-r--r--",
                        "SUBSYSTEM==\"net\", ACTION==\"add\", ATTR{address}==\"" + macs.get(0) + "\", NAME=\"wan\"",
                        "SUBSYSTEM==\"net\", ACTION==\"add\", ATTR{address}==\"" + macs.get(1) + "\", NAME=\"lan\"",
                        "SUBSYSTEM==\"net\", ACTION==\"add\", ATTR{address}==\"" + macs.get(2) + "\", NAME=\"dmz\""
                );
                logger.info("WAN：{}", macs.get(0));
                logger.info("LAN：{}", macs.get(1));
                logger.info("DMZ：{}", macs.get(2));
            } else {
                logger.error("网卡个数不对，应该为2个或3个");
                System.exit(-1);
            }
            CommandHelper.execute("udevadm trigger");
            logger.info("成功配置网卡");
        }

    }

    @Value("${server.wan}")
    private String wan;
    private final static Logger logger = LoggerFactory.getLogger(BootCheck.class);

    public void setWan(String wan) {
        this.wan = wan;
    }
}
