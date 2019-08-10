package org.blackey.model.request;

public class AdvertiseRequest {


    public static final Integer OPEN_TIME_ANY = 0;
    public static final Integer OPEN_TIME_CUSTOM = 1;

    private Long adId;
    /**
     * 用户id
     */
    private Long userId;


    /**
     * 货币类型
     */
    private Long currencyId;




    /**
     * 地区
     */
    private Long countryId;

    /**
     * 溢价
     */
    private String margin;
    /**
     * 参考价格
     */
    private String referenPrice;
    /**
     * 价格
     */
    private String price;
    /**
     * 广告最低可交易价格
     */
    private String minPrice;

    /**
     * 总库存 出售数量
     */
    private String totalInventory;


    /**
     * 最小量
     */
    private String minLimit;
    /**
     * 最大量
     */
    private String maxLimit;
    /**
     * 付款期限（分钟）
     */
    private Integer paymentWindow;
    /**
     * 1:仅限实名认证的交易者 0:任何买家
     */
    private Integer isOnlyVerified;
    /**
     * 1:仅限受信任的交易者 0：任何买家
     */
    private Integer isOnlyTrusted;
    /**
     * 留言
     */
    private String leaveWord;
    /**
     * 0:任何时间；1:自定义
     */
    private Integer openTime;
    /**
     * 卖家门罗币地址
     */
    private String sellerAddress;
    /**
     * 保证金地址
     */
    private String cashDepositAddress;
    /**
     * 保证金地址支付密码（加密）
     */
    private String cashDepositAddressPasswd;

    /**
     * 是否撤单
     */
    private Integer isRevoke;

    /**
     * 支付方式1
     */
    private Long   paymentModeId1;

    /**
     * 支付方式2
     */
    private Long   paymentModeId2;

    /**
     * 支付方式3
     */
    private Long   paymentModeId3;

    /**
     * 每周日开始时间
     */
    private String sunSatrtTime;
    /**
     * 每周日结束时间
     */
    private String sunEndTime;

    /**
     * 每周1开始时间
     */
    private String monSatrtTime;
    /**
     * 每周1结束时间
     */
    private String monEndTime;

    /**
     * 每周2开始时间
     */
    private String tuesSatrtTime;

    /**
     * 每周2结束时间
     */
    private String tuesEndTime;


    /**
     * 每周3开始时间
     */
    private String wedSatrtTime;
    /**
     * 每周3结束时间
     */
    private String wedEndTime;

    /**
     * 周4
     */
    private String thurSatrtTime;
    private String thurEndTime;

    /**
     * 周五
     */
    private String friSatrtTime;
    private String friEndTime;

    /**
     * 周六
     */
    private String satSatrtTime;
    private String satEndTime;


    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }


    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public String getReferenPrice() {
        return referenPrice;
    }

    public void setReferenPrice(String referenPrice) {
        this.referenPrice = referenPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getTotalInventory() {
        return totalInventory;
    }

    public void setTotalInventory(String totalInventory) {
        this.totalInventory = totalInventory;
    }

    public String getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(String minLimit) {
        this.minLimit = minLimit;
    }

    public String getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(String maxLimit) {
        this.maxLimit = maxLimit;
    }

    public Integer getPaymentWindow() {
        return paymentWindow;
    }

    public void setPaymentWindow(Integer paymentWindow) {
        this.paymentWindow = paymentWindow;
    }

    public Integer getIsOnlyVerified() {
        return isOnlyVerified;
    }

    public void setIsOnlyVerified(Integer isOnlyVerified) {
        this.isOnlyVerified = isOnlyVerified;
    }

    public Integer getIsOnlyTrusted() {
        return isOnlyTrusted;
    }

    public void setIsOnlyTrusted(Integer isOnlyTrusted) {
        this.isOnlyTrusted = isOnlyTrusted;
    }

    public String getLeaveWord() {
        return leaveWord;
    }

    public void setLeaveWord(String leaveWord) {
        this.leaveWord = leaveWord;
    }

    public Integer getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Integer openTime) {
        this.openTime = openTime;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public String getCashDepositAddress() {
        return cashDepositAddress;
    }

    public void setCashDepositAddress(String cashDepositAddress) {
        this.cashDepositAddress = cashDepositAddress;
    }

    public String getCashDepositAddressPasswd() {
        return cashDepositAddressPasswd;
    }

    public void setCashDepositAddressPasswd(String cashDepositAddressPasswd) {
        this.cashDepositAddressPasswd = cashDepositAddressPasswd;
    }

    public Integer getIsRevoke() {
        return isRevoke;
    }

    public void setIsRevoke(Integer isRevoke) {
        this.isRevoke = isRevoke;
    }

    public Long getPaymentModeId1() {
        return paymentModeId1;
    }

    public void setPaymentModeId1(Long paymentModeId1) {
        this.paymentModeId1 = paymentModeId1;
    }

    public Long getPaymentModeId2() {
        return paymentModeId2;
    }

    public void setPaymentModeId2(Long paymentModeId2) {
        this.paymentModeId2 = paymentModeId2;
    }

    public Long getPaymentModeId3() {
        return paymentModeId3;
    }

    public void setPaymentModeId3(Long paymentModeId3) {
        this.paymentModeId3 = paymentModeId3;
    }

    public String getSunSatrtTime() {
        return sunSatrtTime;
    }

    public void setSunSatrtTime(String sunSatrtTime) {
        this.sunSatrtTime = sunSatrtTime;
    }

    public String getSunEndTime() {
        return sunEndTime;
    }

    public void setSunEndTime(String sunEndTime) {
        this.sunEndTime = sunEndTime;
    }

    public String getMonSatrtTime() {
        return monSatrtTime;
    }

    public void setMonSatrtTime(String monSatrtTime) {
        this.monSatrtTime = monSatrtTime;
    }

    public String getMonEndTime() {
        return monEndTime;
    }

    public void setMonEndTime(String monEndTime) {
        this.monEndTime = monEndTime;
    }

    public String getTuesSatrtTime() {
        return tuesSatrtTime;
    }

    public void setTuesSatrtTime(String tuesSatrtTime) {
        this.tuesSatrtTime = tuesSatrtTime;
    }

    public String getTuesEndTime() {
        return tuesEndTime;
    }

    public void setTuesEndTime(String tuesEndTime) {
        this.tuesEndTime = tuesEndTime;
    }

    public String getWedSatrtTime() {
        return wedSatrtTime;
    }

    public void setWedSatrtTime(String wedSatrtTime) {
        this.wedSatrtTime = wedSatrtTime;
    }

    public String getWedEndTime() {
        return wedEndTime;
    }

    public void setWedEndTime(String wedEndTime) {
        this.wedEndTime = wedEndTime;
    }

    public String getThurSatrtTime() {
        return thurSatrtTime;
    }

    public void setThurSatrtTime(String thurSatrtTime) {
        this.thurSatrtTime = thurSatrtTime;
    }

    public String getThurEndTime() {
        return thurEndTime;
    }

    public void setThurEndTime(String thurEndTime) {
        this.thurEndTime = thurEndTime;
    }

    public String getFriSatrtTime() {
        return friSatrtTime;
    }

    public void setFriSatrtTime(String friSatrtTime) {
        this.friSatrtTime = friSatrtTime;
    }

    public String getFriEndTime() {
        return friEndTime;
    }

    public void setFriEndTime(String friEndTime) {
        this.friEndTime = friEndTime;
    }

    public String getSatSatrtTime() {
        return satSatrtTime;
    }

    public void setSatSatrtTime(String satSatrtTime) {
        this.satSatrtTime = satSatrtTime;
    }

    public String getSatEndTime() {
        return satEndTime;
    }

    public void setSatEndTime(String satEndTime) {
        this.satEndTime = satEndTime;
    }
}
