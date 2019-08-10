package org.blackey.model.request;

import org.blackey.entity.UserPaymentMode;

public class UserPaymentModeRequest {

    private Long userPaymentModeId;

    /**
     * 货币类型
     */
    private String currencyId;


    /**
     * 支付方式
     */
    private String paymentModeId;

    /**
     * 户名
     */
    private String accountName;
    /**
     * 账号
     */
    private String accountNo;
    /**
     * 开户分支行
     */
    private String bankId;

    /**
     * 开户行
     */
    private String openingBank;
    /**
     * 用于收款 1：是，0：否
     */
    private Integer forCollection;
    /**
     * 用于支付 1：是，0：否
     */
    private Integer forPayment;
    /**
     * 二维码
     */
    private String qrCode;

    public UserPaymentModeRequest(UserPaymentMode entity) {
        setUserPaymentModeId(entity.getUserPaymentModeId());
        setPaymentModeId(entity.getPaymentModeId().toString());
        setAccountName(entity.getAccountName());
        setAccountNo(entity.getAccountNo());
        setBankId(entity.getBankName());
        setOpeningBank(entity.getOpeningBank());
        setForCollection(entity.getForCollection());
        setForPayment(entity.getForPayment());

    }

    public UserPaymentModeRequest() {
    }

    public Long getUserPaymentModeId() {
        return userPaymentModeId;
    }

    public void setUserPaymentModeId(Long userPaymentModeId) {
        this.userPaymentModeId = userPaymentModeId;
    }

    public String getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(String paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getOpeningBank() {
        return openingBank;
    }

    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank;
    }

    public Integer getForCollection() {
        return forCollection;
    }

    public void setForCollection(Integer forCollection) {
        this.forCollection = forCollection;
    }

    public Integer getForPayment() {
        return forPayment;
    }

    public void setForPayment(Integer forPayment) {
        this.forPayment = forPayment;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }


}
