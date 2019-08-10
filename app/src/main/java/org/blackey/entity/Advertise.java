package org.blackey.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;


public class Advertise implements Parcelable {

    private Long adId;
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户名称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userPortrait;

    /**
     * 用户是否在线
     */
    private Integer userIsOnline;

    /**
     * 用户是否实名认证
     */
    private Integer userIsIdVerified;

    /**
     * 交易次数
     */
    private Integer userCountTrades;

    /**
     * 好评
     */
    private Integer userCountPraise;

    /**
     * 货币类型
     */
    private Long currencyId;

    /**
     * 货币类型
     */
    private String currencyName;

    /**
     * 地区
     */
    private Long countryId;
    /**
     * 地区
     */
    private String countryCode;
    /**
     * 溢价
     */
    private BigDecimal margin;
    /**
     * 参考价格
     */
    private BigDecimal referenPrice;
    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 总库存 出售数量
     */
    private BigDecimal totalInventory;

    /**
     * 库存数量
     */
    private BigDecimal inventory;

    /**
     * 广告最低可交易价格
     */
    private BigDecimal minPrice;
    /**
     * 最小量
     */
    private BigDecimal minLimit;
    /**
     * 最大量
     */
    private BigDecimal maxLimit;
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
     * 0:待确认 1:上架 2:下架
     */
    private Integer adStatus;

    /**
     * 支付方式1
     */
    private Long   paymentModeId1;
    private String paymentCode1;
    private String paymentName1;
    private String paymentIcon1;
    /**
     * 支付方式2
     */
    private Long   paymentModeId2;
    private String paymentCode2;
    private String paymentName2;
    private String paymentIcon2;
    /**
     * 支付方式3
     */
    private Long   paymentModeId3;
    private String paymentCode3;
    private String paymentName3;
    private String paymentIcon3;

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
     * 周四
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

    private String createTime;

    public Advertise() {
    }


    protected Advertise(Parcel in) {
        if (in.readByte() == 0) {
            adId = null;
        } else {
            adId = in.readLong();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
        userNickname = in.readString();
        userPortrait = in.readString();
        if (in.readByte() == 0) {
            userIsOnline = null;
        } else {
            userIsOnline = in.readInt();
        }
        if (in.readByte() == 0) {
            userIsIdVerified = null;
        } else {
            userIsIdVerified = in.readInt();
        }
        if (in.readByte() == 0) {
            userCountTrades = null;
        } else {
            userCountTrades = in.readInt();
        }
        if (in.readByte() == 0) {
            userCountPraise = null;
        } else {
            userCountPraise = in.readInt();
        }
        if (in.readByte() == 0) {
            currencyId = null;
        } else {
            currencyId = in.readLong();
        }
        currencyName = in.readString();

       margin = new BigDecimal(in.readString());
       referenPrice = new BigDecimal(in.readString());
       price = new BigDecimal(in.readString());
       totalInventory = new BigDecimal(in.readString());
       inventory = new BigDecimal(in.readString());
       minPrice = new BigDecimal(in.readString());
       minLimit = new BigDecimal(in.readString());
       maxLimit = new BigDecimal(in.readString());

        if (in.readByte() == 0) {
            countryId = null;
        } else {
            countryId = in.readLong();
        }
        countryCode = in.readString();
        if (in.readByte() == 0) {
            paymentWindow = null;
        } else {
            paymentWindow = in.readInt();
        }
        if (in.readByte() == 0) {
            isOnlyVerified = null;
        } else {
            isOnlyVerified = in.readInt();
        }
        if (in.readByte() == 0) {
            isOnlyTrusted = null;
        } else {
            isOnlyTrusted = in.readInt();
        }
        leaveWord = in.readString();
        if (in.readByte() == 0) {
            openTime = null;
        } else {
            openTime = in.readInt();
        }
        sellerAddress = in.readString();
        cashDepositAddress = in.readString();
        cashDepositAddressPasswd = in.readString();
        if (in.readByte() == 0) {
            adStatus = null;
        } else {
            adStatus = in.readInt();
        }
        if (in.readByte() == 0) {
            paymentModeId1 = null;
        } else {
            paymentModeId1 = in.readLong();
        }
        paymentCode1 = in.readString();
        paymentName1 = in.readString();
        paymentIcon1 = in.readString();
        if (in.readByte() == 0) {
            paymentModeId2 = null;
        } else {
            paymentModeId2 = in.readLong();
        }
        paymentCode2 = in.readString();
        paymentName2 = in.readString();
        paymentIcon2 = in.readString();
        if (in.readByte() == 0) {
            paymentModeId3 = null;
        } else {
            paymentModeId3 = in.readLong();
        }
        paymentCode3 = in.readString();
        paymentName3 = in.readString();
        paymentIcon3 = in.readString();
        sunSatrtTime = in.readString();
        sunEndTime = in.readString();
        monSatrtTime = in.readString();
        monEndTime = in.readString();
        tuesSatrtTime = in.readString();
        tuesEndTime = in.readString();
        wedSatrtTime = in.readString();
        wedEndTime = in.readString();
        thurSatrtTime = in.readString();
        thurEndTime = in.readString();
        friSatrtTime = in.readString();
        friEndTime = in.readString();
        satSatrtTime = in.readString();
        satEndTime = in.readString();
        createTime = in.readString();
    }

    public static final Creator<Advertise> CREATOR = new Creator<Advertise>() {
        @Override
        public Advertise createFromParcel(Parcel in) {
            return new Advertise(in);
        }

        @Override
        public Advertise[] newArray(int size) {
            return new Advertise[size];
        }
    };

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

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public Integer getUserIsOnline() {
        return userIsOnline;
    }

    public void setUserIsOnline(Integer userIsOnline) {
        this.userIsOnline = userIsOnline;
    }

    public Integer getUserIsIdVerified() {
        return userIsIdVerified;
    }

    public void setUserIsIdVerified(Integer userIsIdVerified) {
        this.userIsIdVerified = userIsIdVerified;
    }

    public Integer getUserCountTrades() {
        return userCountTrades;
    }

    public void setUserCountTrades(Integer userCountTrades) {
        this.userCountTrades = userCountTrades;
    }

    public Integer getUserCountPraise() {
        return userCountPraise;
    }

    public void setUserCountPraise(Integer userCountPraise) {
        this.userCountPraise = userCountPraise;
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public BigDecimal getMargin() {
        return margin;
    }

    public void setMargin(BigDecimal margin) {
        this.margin = margin;
    }

    public BigDecimal getReferenPrice() {
        return referenPrice;
    }

    public void setReferenPrice(BigDecimal referenPrice) {
        this.referenPrice = referenPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTotalInventory() {
        return totalInventory;
    }

    public void setTotalInventory(BigDecimal totalInventory) {
        this.totalInventory = totalInventory;
    }

    public BigDecimal getInventory() {
        return inventory;
    }

    public void setInventory(BigDecimal inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMinLimit() {
        return minLimit;
    }

    public void setMinLimit(BigDecimal minLimit) {
        this.minLimit = minLimit;
    }

    public BigDecimal getMaxLimit() {
        return maxLimit;
    }

    public void setMaxLimit(BigDecimal maxLimit) {
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

    public Integer getAdStatus() {
        return adStatus;
    }

    public void setAdStatus(Integer adStatus) {
        this.adStatus = adStatus;
    }

    public Long getPaymentModeId1() {
        return paymentModeId1;
    }

    public void setPaymentModeId1(Long paymentModeId1) {
        this.paymentModeId1 = paymentModeId1;
    }

    public String getPaymentCode1() {
        return paymentCode1;
    }

    public void setPaymentCode1(String paymentCode1) {
        this.paymentCode1 = paymentCode1;
    }

    public String getPaymentName1() {
        return paymentName1;
    }

    public void setPaymentName1(String paymentName1) {
        this.paymentName1 = paymentName1;
    }

    public String getPaymentIcon1() {
        return paymentIcon1;
    }

    public void setPaymentIcon1(String paymentIcon1) {
        this.paymentIcon1 = paymentIcon1;
    }

    public Long getPaymentModeId2() {
        return paymentModeId2;
    }

    public void setPaymentModeId2(Long paymentModeId2) {
        this.paymentModeId2 = paymentModeId2;
    }

    public String getPaymentCode2() {
        return paymentCode2;
    }

    public void setPaymentCode2(String paymentCode2) {
        this.paymentCode2 = paymentCode2;
    }

    public String getPaymentName2() {
        return paymentName2;
    }

    public void setPaymentName2(String paymentName2) {
        this.paymentName2 = paymentName2;
    }

    public String getPaymentIcon2() {
        return paymentIcon2;
    }

    public void setPaymentIcon2(String paymentIcon2) {
        this.paymentIcon2 = paymentIcon2;
    }

    public Long getPaymentModeId3() {
        return paymentModeId3;
    }

    public void setPaymentModeId3(Long paymentModeId3) {
        this.paymentModeId3 = paymentModeId3;
    }

    public String getPaymentCode3() {
        return paymentCode3;
    }

    public void setPaymentCode3(String paymentCode3) {
        this.paymentCode3 = paymentCode3;
    }

    public String getPaymentName3() {
        return paymentName3;
    }

    public void setPaymentName3(String paymentName3) {
        this.paymentName3 = paymentName3;
    }

    public String getPaymentIcon3() {
        return paymentIcon3;
    }

    public void setPaymentIcon3(String paymentIcon3) {
        this.paymentIcon3 = paymentIcon3;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (adId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(adId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userId);
        }
        dest.writeString(userNickname);
        dest.writeString(userPortrait);
        if (userIsOnline == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userIsOnline);
        }
        if (userIsIdVerified == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userIsIdVerified);
        }
        if (userCountTrades == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userCountTrades);
        }
        if (userCountPraise == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userCountPraise);
        }
        if (currencyId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(currencyId);
        }
        dest.writeString(currencyName);

        dest.writeString(margin == null? "0": margin.toPlainString());
        dest.writeString(referenPrice == null? "0": margin.toPlainString());
        dest.writeString(price == null? "0": margin.toPlainString());
        dest.writeString(totalInventory == null? "0": margin.toPlainString());
        dest.writeString(inventory == null? "0": margin.toPlainString());
        dest.writeString(minPrice == null? "0": margin.toPlainString());
        dest.writeString(minLimit == null? "0": margin.toPlainString());
        dest.writeString(maxLimit == null? "0": margin.toPlainString());


        if (countryId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(countryId);
        }
        dest.writeString(countryCode);
        if (paymentWindow == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(paymentWindow);
        }
        if (isOnlyVerified == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isOnlyVerified);
        }
        if (isOnlyTrusted == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isOnlyTrusted);
        }
        dest.writeString(leaveWord);
        if (openTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(openTime);
        }
        dest.writeString(sellerAddress);
        dest.writeString(cashDepositAddress);
        dest.writeString(cashDepositAddressPasswd);
        if (adStatus == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(adStatus);
        }
        if (paymentModeId1 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(paymentModeId1);
        }
        dest.writeString(paymentCode1);
        dest.writeString(paymentName1);
        dest.writeString(paymentIcon1);
        if (paymentModeId2 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(paymentModeId2);
        }
        dest.writeString(paymentCode2);
        dest.writeString(paymentName2);
        dest.writeString(paymentIcon2);
        if (paymentModeId3 == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(paymentModeId3);
        }
        dest.writeString(paymentCode3);
        dest.writeString(paymentName3);
        dest.writeString(paymentIcon3);
        dest.writeString(sunSatrtTime);
        dest.writeString(sunEndTime);
        dest.writeString(monSatrtTime);
        dest.writeString(monEndTime);
        dest.writeString(tuesSatrtTime);
        dest.writeString(tuesEndTime);
        dest.writeString(wedSatrtTime);
        dest.writeString(wedEndTime);
        dest.writeString(thurSatrtTime);
        dest.writeString(thurEndTime);
        dest.writeString(friSatrtTime);
        dest.writeString(friEndTime);
        dest.writeString(satSatrtTime);
        dest.writeString(satEndTime);
        dest.writeString(createTime);
    }
}
