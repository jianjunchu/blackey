package org.blackey.entity;

import android.os.Parcel;
import android.os.Parcelable;

import me.goldze.mvvmhabit.binding.viewadapter.spinner.IKeyAndValue;

/**
 * 货币字典
 */
public class Currency implements Parcelable, IKeyAndValue {

    private Long currencyId;
    private String currencyNameEn;
    private String currencyNameCn;

    public Currency() {
    }

    protected Currency(Parcel in) {
        if (in.readByte() == 0) {
            currencyId = null;
        } else {
            currencyId = in.readLong();
        }
        currencyNameEn = in.readString();
        currencyNameCn = in.readString();
    }

    public static final Creator<Currency> CREATOR = new Creator<Currency>() {
        @Override
        public Currency createFromParcel(Parcel in) {
            return new Currency(in);
        }

        @Override
        public Currency[] newArray(int size) {
            return new Currency[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (currencyId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(currencyId);
        }
        dest.writeString(currencyNameEn);
        dest.writeString(currencyNameCn);
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getCurrencyNameEn() {
        return currencyNameEn;
    }

    public void setCurrencyNameEn(String currencyNameEn) {
        this.currencyNameEn = currencyNameEn;
    }

    public String getCurrencyNameCn() {
        return currencyNameCn;
    }

    public void setCurrencyNameCn(String currencyNameCn) {
        this.currencyNameCn = currencyNameCn;
    }

    @Override
    public String getKey() {
        return currencyNameEn;
    }

    @Override
    public String getValue() {
        return currencyId.toString();
    }
}
