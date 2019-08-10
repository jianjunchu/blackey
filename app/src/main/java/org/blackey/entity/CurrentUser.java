package org.blackey.entity;

public class CurrentUser {

    private Long userId;
    /**
     * 国家代码
     */
    private String nationCode;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 昵称
     */
    private String nickname;

    /**
     * 是否已实名认证（0：未认证 ;1:已实名认证 ）
     */
    private Integer isIdVerified;

    /**
     * 头像url地址
     */
    private String portrait;

    /**
     * 推荐码
     */
    private String inviteCode;
    /**
     * 推荐用户ID
     */
    private Long inviterId;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 交易次数
     */
    private Integer trades;
    /**
     * 信用评分
     */
    private Integer creditScore;
    /**
     * 好评
     */
    private String praise;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNationCode() {
        return nationCode;
    }

    public void setNationCode(String nationCode) {
        this.nationCode = nationCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getIsIdVerified() {
        return isIdVerified;
    }

    public void setIsIdVerified(Integer isIdVerified) {
        this.isIdVerified = isIdVerified;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public Long getInviterId() {
        return inviterId;
    }

    public void setInviterId(Long inviterId) {
        this.inviterId = inviterId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTrades() {
        return trades;
    }

    public void setTrades(Integer trades) {
        this.trades = trades;
    }

    public Integer getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(Integer creditScore) {
        this.creditScore = creditScore;
    }

    public String getPraise() {
        return praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }
}
