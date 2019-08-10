package org.blackey.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

import me.goldze.mvvmhabit.binding.viewadapter.spinner.IKeyAndValue;

public class UserPaymentMode  implements Parcelable , IKeyAndValue {

    private Long userPaymentModeId;
    /**
     * 支付方式
     */
    private Long paymentModeId;

    /**
     * 支付方式
     */
    private String paymentModeCode;

    /**
     * 支付方式
     */
    private String paymentModeName;

    private String  paymentModeIcon;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 货币类型
     */
    private Long currencyId;

    /**
     * 货币类型
     */
    private String currencyName;

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
    private Long bankId;

    private String bankName;

    private String bankSwiftCode;
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

    public UserPaymentMode() {
    }


    protected UserPaymentMode(Parcel in) {
        if (in.readByte() == 0) {
            userPaymentModeId = null;
        } else {
            userPaymentModeId = in.readLong();
        }
        if (in.readByte() == 0) {
            paymentModeId = null;
        } else {
            paymentModeId = in.readLong();
        }
        paymentModeCode = in.readString();
        paymentModeName = in.readString();
        paymentModeIcon = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
        if (in.readByte() == 0) {
            currencyId = null;
        } else {
            currencyId = in.readLong();
        }
        currencyName = in.readString();
        accountName = in.readString();
        accountNo = in.readString();
        if (in.readByte() == 0) {
            bankId = null;
        } else {
            bankId = in.readLong();
        }
        bankName = in.readString();
        bankSwiftCode = in.readString();
        openingBank = in.readString();
        if (in.readByte() == 0) {
            forCollection = null;
        } else {
            forCollection = in.readInt();
        }
        if (in.readByte() == 0) {
            forPayment = null;
        } else {
            forPayment = in.readInt();
        }
        qrCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (userPaymentModeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userPaymentModeId);
        }
        if (paymentModeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(paymentModeId);
        }
        dest.writeString(paymentModeCode);
        dest.writeString(paymentModeName);
        dest.writeString(paymentModeIcon);
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userId);
        }

        if (currencyId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(currencyId);
        }
        dest.writeString(currencyName);


        dest.writeString(accountName);
        dest.writeString(accountNo);
        if (bankId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(bankId);
        }
        dest.writeString(bankName);
        dest.writeString(bankSwiftCode);
        dest.writeString(openingBank);
        if (forCollection == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(forCollection);
        }
        if (forPayment == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(forPayment);
        }
        dest.writeString(qrCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserPaymentMode> CREATOR = new Creator<UserPaymentMode>() {
        @Override
        public UserPaymentMode createFromParcel(Parcel in) {
            return new UserPaymentMode(in);
        }

        @Override
        public UserPaymentMode[] newArray(int size) {
            return new UserPaymentMode[size];
        }
    };

    public Long getUserPaymentModeId() {
        return userPaymentModeId;
    }

    public void setUserPaymentModeId(Long userPaymentModeId) {
        this.userPaymentModeId = userPaymentModeId;
    }

    public Long getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(Long paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public String getPaymentModeCode() {
        return paymentModeCode;
    }

    public void setPaymentModeCode(String paymentModeCode) {
        this.paymentModeCode = paymentModeCode;
    }

    public String getPaymentModeName() {
        return paymentModeName;
    }

    public void setPaymentModeName(String paymentModeName) {
        this.paymentModeName = paymentModeName;
    }

    public String getPaymentModeIcon() {
        return paymentModeIcon;
    }

    public void setPaymentModeIcon(String paymentModeIcon) {
        this.paymentModeIcon = paymentModeIcon;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankSwiftCode() {
        return bankSwiftCode;
    }

    public void setBankSwiftCode(String bankSwiftCode) {
        this.bankSwiftCode = bankSwiftCode;
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

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPaymentMode that = (UserPaymentMode) o;
        return Objects.equals(userPaymentModeId, that.userPaymentModeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPaymentModeId);
    }
}
