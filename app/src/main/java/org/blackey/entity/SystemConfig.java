package org.blackey.entity;

import java.math.BigDecimal;

public class SystemConfig {

    /**
     * 邮箱发送人
     */
    private String mailFromName;
    /**
     * 邮箱SMTP HOST
     */
    private String mailSmtpHost;
    /**
     * SMTP身份认证
     */
    private Integer mailSmtpAuth;
    /**
     * SMTP超时时间
     */
    private Integer mailSmtpTimeout;
    /**
     * 发送邮箱
     */
    private String mailSmtpFrom;
    /**
     * SMTP用户名
     */
    private String mailSmtpUsername;
    /**
     * SMTP密码
     */
    private String mailSmtpPassword;
    /**
     * 发送短信APPID
     */
    private String smsAppid;
    /**
     * 发送短信APPKEY
     */
    private String smsAppkey;
    /**
     * 中文模板ID
     */
    private String smsTemplIdZh;
    /**
     * 中文签名
     */
    private String smsSignZh;
    /**
     * 英文模板ID
     */
    private String smsTemplIdEn;
    /**
     * 英文签名
     */
    private String smsSignEn;
    /**
     * OTC交易手续费
     */
    private BigDecimal tradingFee;

    /**
     * 挂单和撤单手续费
     */
    private BigDecimal adFee;

    /**
     * daemon 节点IP
     */
    private String daemonRpcDomain;
    /**
     * daemon 节点端口
     */
    private Integer daemonRpcPort;
    /**
     * daemon 节点用户名
     */
    private String daemonRpcUsername;
    /**
     * daemon 节点密码
     */
    private String daemonRpcPassword;
    /**
     * wallet 节点IP
     */
    private String walletRpcDomain;
    /**
     * wallet 节点端口
     */
    private Integer walletRpcPort;
    /**
     * wallet 节点用户名
     */
    private String walletRpcUsername;
    /**
     * wallet 节点密码
     */
    private String walletRpcPassword;

    public String getMailFromName() {
        return mailFromName;
    }

    public void setMailFromName(String mailFromName) {
        this.mailFromName = mailFromName;
    }

    public String getMailSmtpHost() {
        return mailSmtpHost;
    }

    public void setMailSmtpHost(String mailSmtpHost) {
        this.mailSmtpHost = mailSmtpHost;
    }

    public Integer getMailSmtpAuth() {
        return mailSmtpAuth;
    }

    public void setMailSmtpAuth(Integer mailSmtpAuth) {
        this.mailSmtpAuth = mailSmtpAuth;
    }

    public Integer getMailSmtpTimeout() {
        return mailSmtpTimeout;
    }

    public void setMailSmtpTimeout(Integer mailSmtpTimeout) {
        this.mailSmtpTimeout = mailSmtpTimeout;
    }

    public String getMailSmtpFrom() {
        return mailSmtpFrom;
    }

    public void setMailSmtpFrom(String mailSmtpFrom) {
        this.mailSmtpFrom = mailSmtpFrom;
    }

    public String getMailSmtpUsername() {
        return mailSmtpUsername;
    }

    public void setMailSmtpUsername(String mailSmtpUsername) {
        this.mailSmtpUsername = mailSmtpUsername;
    }

    public String getMailSmtpPassword() {
        return mailSmtpPassword;
    }

    public void setMailSmtpPassword(String mailSmtpPassword) {
        this.mailSmtpPassword = mailSmtpPassword;
    }

    public String getSmsAppid() {
        return smsAppid;
    }

    public void setSmsAppid(String smsAppid) {
        this.smsAppid = smsAppid;
    }

    public String getSmsAppkey() {
        return smsAppkey;
    }

    public void setSmsAppkey(String smsAppkey) {
        this.smsAppkey = smsAppkey;
    }

    public String getSmsTemplIdZh() {
        return smsTemplIdZh;
    }

    public void setSmsTemplIdZh(String smsTemplIdZh) {
        this.smsTemplIdZh = smsTemplIdZh;
    }

    public String getSmsSignZh() {
        return smsSignZh;
    }

    public void setSmsSignZh(String smsSignZh) {
        this.smsSignZh = smsSignZh;
    }

    public String getSmsTemplIdEn() {
        return smsTemplIdEn;
    }

    public void setSmsTemplIdEn(String smsTemplIdEn) {
        this.smsTemplIdEn = smsTemplIdEn;
    }

    public String getSmsSignEn() {
        return smsSignEn;
    }

    public void setSmsSignEn(String smsSignEn) {
        this.smsSignEn = smsSignEn;
    }

    public BigDecimal getTradingFee() {
        return tradingFee;
    }

    public void setTradingFee(BigDecimal tradingFee) {
        this.tradingFee = tradingFee;
    }

    public BigDecimal getAdFee() {
        return adFee;
    }

    public void setAdFee(BigDecimal adFee) {
        this.adFee = adFee;
    }

    public String getDaemonRpcDomain() {
        return daemonRpcDomain;
    }

    public void setDaemonRpcDomain(String daemonRpcDomain) {
        this.daemonRpcDomain = daemonRpcDomain;
    }

    public Integer getDaemonRpcPort() {
        return daemonRpcPort;
    }

    public void setDaemonRpcPort(Integer daemonRpcPort) {
        this.daemonRpcPort = daemonRpcPort;
    }

    public String getDaemonRpcUsername() {
        return daemonRpcUsername;
    }

    public void setDaemonRpcUsername(String daemonRpcUsername) {
        this.daemonRpcUsername = daemonRpcUsername;
    }

    public String getDaemonRpcPassword() {
        return daemonRpcPassword;
    }

    public void setDaemonRpcPassword(String daemonRpcPassword) {
        this.daemonRpcPassword = daemonRpcPassword;
    }

    public String getWalletRpcDomain() {
        return walletRpcDomain;
    }

    public void setWalletRpcDomain(String walletRpcDomain) {
        this.walletRpcDomain = walletRpcDomain;
    }

    public Integer getWalletRpcPort() {
        return walletRpcPort;
    }

    public void setWalletRpcPort(Integer walletRpcPort) {
        this.walletRpcPort = walletRpcPort;
    }

    public String getWalletRpcUsername() {
        return walletRpcUsername;
    }

    public void setWalletRpcUsername(String walletRpcUsername) {
        this.walletRpcUsername = walletRpcUsername;
    }

    public String getWalletRpcPassword() {
        return walletRpcPassword;
    }

    public void setWalletRpcPassword(String walletRpcPassword) {
        this.walletRpcPassword = walletRpcPassword;
    }
}
