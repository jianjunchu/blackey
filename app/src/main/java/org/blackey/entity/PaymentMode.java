package org.blackey.entity;

import android.os.Parcel;
import android.os.Parcelable;

import me.goldze.mvvmhabit.binding.viewadapter.spinner.IKeyAndValue;

public class PaymentMode implements Parcelable , IKeyAndValue {

    private Long paymentModeId;
    private String code;
    private String name;
    private String icon;
    /**
     * 支付方式适合的国家id，空 为适合所有国家
     */
    private Integer countryId;

    /**
     * 区号
     */
    private String areaCode;
    /**
     * 地区code
     */
    private String countryCode;
    /**
     * 地区名称
     */
    private String countryName;

    public PaymentMode() {
    }

    protected PaymentMode(Parcel in) {
        if (in.readByte() == 0) {
            paymentModeId = null;
        } else {
            paymentModeId = in.readLong();
        }
        code = in.readString();
        name = in.readString();
        icon = in.readString();
        if (in.readByte() == 0) {
            countryId = null;
        } else {
            countryId = in.readInt();
        }
        areaCode = in.readString();
        countryCode = in.readString();
        countryName = in.readString();
    }

    public static final Creator<PaymentMode> CREATOR = new Creator<PaymentMode>() {
        @Override
        public PaymentMode createFromParcel(Parcel in) {
            return new PaymentMode(in);
        }

        @Override
        public PaymentMode[] newArray(int size) {
            return new PaymentMode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (paymentModeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(paymentModeId);
        }
        dest.writeString(code);
        dest.writeString(name);
        dest.writeString(icon);
        if (countryId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(countryId);
        }
        dest.writeString(areaCode);
        dest.writeString(countryCode);
        dest.writeString(countryName);
    }

    public Long getPaymentModeId() {
        return paymentModeId;
    }

    public void setPaymentModeId(Long paymentModeId) {
        this.paymentModeId = paymentModeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Integer getCountryId() {
        return countryId;
    }

    public void setCountryId(Integer countryId) {
        this.countryId = countryId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public String getValue() {
        return paymentModeId.toString();
    }
}
