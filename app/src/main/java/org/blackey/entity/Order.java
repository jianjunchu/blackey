package org.blackey.entity;

import android.os.Parcel;
import android.os.Parcelable;


import org.blackey.app.BlackeyApplication;

import java.lang.String;
import java.math.BigDecimal;
import java.util.Date;

public class Order implements Parcelable {

    /***待支付*/
    public static final int ORDER_STATUS_UNPAID = 0;
    /***待确认*/
    public static final int ORDER_STATUS_PREPAID = 1;
    /***交易完成*/
    public static final int ORDER_STATUS_COMPLETE = 2;
    /***交易取消*/
    public static final int ORDER_STATUS_CANCEL = 3;

    /***交易取消*/
    public static final int ORDER_STATUS_TIMEOUT = 4;

    public static final Integer ORDER_TYPE_SELL = 1;

    public static final Integer ORDER_TYPE_BUY = 2;





    private Long orderId;
    /**
     * 订单流水号
     */
    private String orderNo;

    private Integer orderType;

    /**
     * 广告ID
     */
    private Long adId;

    /**
     * 货币类型
     */
    private Long currencyId;

    /**
     * 货币类型
     */
    private String currencyName;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 买家头像
     */
    private String buyerPortrait;

    /**
     * 买家国家代码
     */
    private String buyerNationCode;
    /**
     * 买家手机号
     */
    private String buyerMobile;


    /**
     * 卖家id
     */
    private Long sellerId;

    /**
     * 卖家昵称
     */
    private String sellerNickname;

    /**
     * 卖家头像
     */
    private String sellerPortrait;






    /**
     * 卖家国家代码
     */
    private String sellerNationCode;
    /**
     * 卖家手机号
     */
    private String sellerMobile;


    /**
     * 购买数量
     */
    private BigDecimal quantity;
    /**
     * 交易价格
     */
    private BigDecimal goodsMoney;
    /**
     * 手续费率
     */
    private BigDecimal commissionRate;
    /**
     * 到账数量
     */
    private BigDecimal getQuantity;

    /**
     * 订单应收手续费
     */
    private BigDecimal commissionFee;

    /**
     * 订单总金额
     */
    private BigDecimal totalMoney;

    /**
     * 收货地址
     */
    private String shippingAddress;
    /**
     * 是否支付;0:未支付 1:已支付
     */
    private Integer isPay;
    /**
     * 支付方式
     */
    private Long paymentMode;
    /**
     * 订单状态 0:待支付 1：已支付 2： 已发货 3：交易完成 4：已取消
     */
    private Integer orderStatus;
    /**
     * 是否订单已完结;0：未完结 1:已完结
     */
    private Integer isClosed;
    /**
     * 下单时间
     */
    private Date createTime;
    /**
     * 发货时间
     */
    private String deliveryTime;
    /**
     * 收货时间
     */
    private String receiveTime;
    /**
     * 最后更新时间
     */
    private String updateTime;

    public Order() {
    }


    protected Order(Parcel in) {
        if (in.readByte() == 0) {
            orderId = null;
        } else {
            orderId = in.readLong();
        }
        orderNo = in.readString();
        if (in.readByte() == 0) {
            orderType = null;
        } else {
            orderType = in.readInt();
        }
        if (in.readByte() == 0) {
            adId = null;
        } else {
            adId = in.readLong();
        }
        if (in.readByte() == 0) {
            currencyId = null;
        } else {
            currencyId = in.readLong();
        }
        currencyName = in.readString();
        if (in.readByte() == 0) {
            buyerId = null;
        } else {
            buyerId = in.readLong();
        }
        buyerNickname = in.readString();
        buyerPortrait = in.readString();
        buyerNationCode = in.readString();
        buyerMobile = in.readString();
        if (in.readByte() == 0) {
            sellerId = null;
        } else {
            sellerId = in.readLong();
        }
        sellerNickname = in.readString();
        sellerPortrait = in.readString();

        sellerNationCode = in.readString();
        sellerMobile = in.readString();
        quantity = new BigDecimal(in.readString());
        goodsMoney = new BigDecimal(in.readString());
        commissionRate = new BigDecimal(in.readString());
        getQuantity = new BigDecimal(in.readString());
        commissionFee = new BigDecimal(in.readString());
        totalMoney = new BigDecimal(in.readString());
        shippingAddress = in.readString();
        if (in.readByte() == 0) {
            isPay = null;
        } else {
            isPay = in.readInt();
        }
        if (in.readByte() == 0) {
            paymentMode = null;
        } else {
            paymentMode = in.readLong();
        }
        if (in.readByte() == 0) {
            orderStatus = null;
        } else {
            orderStatus = in.readInt();
        }
        if (in.readByte() == 0) {
            isClosed = null;
        } else {
            isClosed = in.readInt();
        }

        if (in.readLong() == 0) {
            createTime = null;
        } else {
            createTime = new Date(in.readLong());
        }
        deliveryTime = in.readString();
        receiveTime = in.readString();
        updateTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (orderId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(orderId);
        }
        dest.writeString(orderNo);
        if (orderType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(orderType);
        }
        if (adId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(adId);
        }
        if (currencyId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(currencyId);
        }
        dest.writeString(currencyName);
        if (buyerId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(buyerId);
        }
        dest.writeString(buyerNickname);
        dest.writeString(buyerPortrait);
        dest.writeString(buyerNationCode);
        dest.writeString(buyerMobile);
        if (sellerId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(sellerId);
        }
        dest.writeString(sellerNickname);
        dest.writeString(sellerPortrait);

        dest.writeString(sellerNationCode);
        dest.writeString(sellerMobile);
        dest.writeString(quantity == null? "0": quantity.toPlainString());
        dest.writeString(goodsMoney == null? "0": goodsMoney.toPlainString());
        dest.writeString(commissionRate == null? "0": commissionRate.toPlainString());
        dest.writeString(getQuantity == null? "0": getQuantity.toPlainString());
        dest.writeString(commissionFee == null? "0": commissionFee.toPlainString());
        dest.writeString(totalMoney == null? "0": totalMoney.toPlainString());
        dest.writeString(shippingAddress);
        if (isPay == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isPay);
        }
        if (paymentMode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(paymentMode);
        }
        if (orderStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(orderStatus);
        }
        if (isClosed == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isClosed);
        }

        if(createTime == null){
            dest.writeLong(0);
        }else{
            dest.writeLong(createTime.getTime());
        }


        dest.writeString(deliveryTime);
        dest.writeString(receiveTime);
        dest.writeString(updateTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getAdId() {
        return adId;
    }

    public void setAdId(Long adId) {
        this.adId = adId;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerNickname() {
        return buyerNickname;
    }

    public void setBuyerNickname(String buyerNickname) {
        this.buyerNickname = buyerNickname;
    }

    public String getBuyerPortrait() {
        return buyerPortrait;
    }

    public void setBuyerPortrait(String buyerPortrait) {
        this.buyerPortrait = buyerPortrait;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerNickname() {
        return sellerNickname;
    }

    public void setSellerNickname(String sellerNickname) {
        this.sellerNickname = sellerNickname;
    }

    public String getSellerPortrait() {
        return sellerPortrait;
    }

    public void setSellerPortrait(String sellerPortrait) {
        this.sellerPortrait = sellerPortrait;
    }



    public String getSellerNationCode() {
        return sellerNationCode;
    }

    public void setSellerNationCode(String sellerNationCode) {
        this.sellerNationCode = sellerNationCode;
    }

    public String getSellerMobile() {
        return sellerMobile;
    }

    public void setSellerMobile(String sellerMobile) {
        this.sellerMobile = sellerMobile;
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

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(BigDecimal commissionRate) {
        this.commissionRate = commissionRate;
    }

    public BigDecimal getGetQuantity() {
        return getQuantity;
    }

    public void setGetQuantity(BigDecimal getQuantity) {
        this.getQuantity = getQuantity;
    }

    public BigDecimal getCommissionFee() {
        return commissionFee;
    }

    public void setCommissionFee(BigDecimal commissionFee) {
        this.commissionFee = commissionFee;
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

    public Integer getIsPay() {
        return isPay;
    }

    public void setIsPay(Integer isPay) {
        this.isPay = isPay;
    }

    public Long getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(Long paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getIsClosed() {
        return isClosed;
    }

    public void setIsClosed(Integer isClosed) {
        this.isClosed = isClosed;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getBuyerNationCode() {
        return buyerNationCode;
    }

    public void setBuyerNationCode(String buyerNationCode) {
        this.buyerNationCode = buyerNationCode;
    }

    public String getBuyerMobile() {
        return buyerMobile;
    }

    public void setBuyerMobile(String buyerMobile) {
        this.buyerMobile = buyerMobile;
    }

    public boolean isSellOrder() {
        if(BlackeyApplication.getCurrent()==null){
            return false;
        }else{
           return BlackeyApplication.getCurrent().getUserId().equals(getSellerId());
        }
    }

    public boolean isBuyOrder() {
        if(BlackeyApplication.getCurrent()==null){
            return false;
        }else{
            return BlackeyApplication.getCurrent().getUserId().equals(getBuyerId());
        }
    }
}
