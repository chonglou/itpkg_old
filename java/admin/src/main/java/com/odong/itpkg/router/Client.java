package com.odong.itpkg.router;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * 路由器客户端抽象类 只定义基本功能
 * <p/>
 * 实现类名称：type.name()+ClientImpl 在包com.odong.itpkg.router.impl下
 * 如：TPLINK_WR841NClientImpl
 * <p/>
 * 请确认命名风格一致，以便自动处理
 * <p/>
 * <p/>
 * http client 建议使用 Apache HttpComponents
 * 教程：http://hc.apache.org/httpcomponents-client-ga/tutorial/html/
 * maven依赖添加(pom.xml):
 * <dependency>
 * <groupId>org.apache.httpcomponents</groupId>
 * <artifactId>httpclient</artifactId>
 * <version>4.2.5</version>
 * </dependency>
 * <p/>
 * <p/>
 * Logger系统使用slf4j
 * maven依赖添加（pom.xml）:
 * <dependency>
 * <groupId>org.slf4j</groupId>
 * <artifactId>slf4j-api</artifactId>
 * <version>1.7.5</version>
 * </dependency>
 * 用法：
 * 类成员添加： private final static Logger logger = LoggerFactory.getLogger(当前类名.class); *
 * logger.debug("");
 * logger.info("");
 * logger.warn("");
 * logger.error("");
 * <p/>
 * <p/>
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 上午9:30
 */
public abstract class Client {
    /**
     * 构造函数
     *
     * @param url      http://x.x.x.x:port
     * @param username 用户名
     * @param password 密码
     */
    public Client(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * @return 路由器状态（用于心跳检测）
     */
    public abstract Map<String, String> status() throws IOException;

    /**
     * 设置wan ip为DHCP获取
     */
    public abstract void setWan() throws IOException;

    /**
     * 设置wan ip为固定ip
     *
     * @param address ip地址
     * @param netmask 子网掩码
     * @param gateway 网关
     * @param dns1    主dns
     * @param dns2    备用dns
     */
    public abstract void setWanStatic(String address, String netmask, String gateway, String dns1, String dns2) throws IOException;

    /**
     * 设置wan ip为pppoe获取
     *
     * @param username 用户名
     * @param password 密码
     */
    public abstract void setWanPppoe(String username, String password) throws IOException;

    /**
     * LAN口设置
     *
     * @param address 内网ip地址
     * @param netmask 子网掩码
     */
    public abstract void setLan(String address, String netmask) throws IOException;

    /**
     * 设置无限
     *
     * @param ssid     SSID
     * @param password 无线密码（	WPA-PSK/WPA2-PSK  AES） null表示是开放式网络
     * @param enable   true-开启 false-关闭
     */
    public abstract void setWireless(String ssid, String password, boolean enable) throws IOException;

    /**
     * 设置DHCP服务器
     *
     * @param begin  起始IP
     * @param end    结束IP
     * @param enable true-开启 false-关闭
     */
    public abstract void setDhcp(String begin, String end, boolean enable) throws IOException;

    /**
     * 设置NAT
     *
     * @param nats NAT信息数组
     */
    public abstract void setNat(Nat... nats) throws IOException;

    /**
     * 绑定mac到ip
     *
     * @param macs {mac:ip}映射
     */
    public abstract void bindMac2Ip(Map<String, String> macs) throws IOException;

    /**
     * @return 主机列表（PC？ 笔记本？ 手机？）
     */
    public abstract Set<Host> listHost() throws IOException;

    /**
     * 重启路由
     */
    public abstract void reboot() throws IOException;

    protected final String url;
    protected final String username;
    protected final String password;


}
