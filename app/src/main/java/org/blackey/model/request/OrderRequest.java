package org.blackey.model.request;

import java.math.BigDecimal;

public class OrderRequest {

    /**
     * 广告ID
     */
    private Long adId;

    /**
     * 货币类型
     */
    private Long currencyId;

    /**
     * 购买数量
     */
    private BigDecimal quantity;

    /**
     * 交易价格
     */
    private BigDecimal goodsMoney;

    /**
     * 订单总金额
     */
    private BigDecimal totalMoney;

    /**
     * 收货地址
     */
    private String shippingAddress;

    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getGoodsMoney() {
        return goodsMoney;
    }

    public void setGoodsMoney(BigDecimal goodsMoney) {
        this.goodsMoney = goodsMoney;
    }

    public BigDecimal getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(BigDecimal totalMoney) {
        this.totalMoney = totalMoney;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }
}
