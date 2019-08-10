package org.blackey.entity;

import android.os.Parcel;
import android.os.Parcelable;

import me.goldze.mvvmhabit.binding.viewadapter.spinner.IKeyAndValue;

public class Bank implements Parcelable , IKeyAndValue {

    /**
     * 银行ID
     */
    private Long bankId;
    private String bankNameEn;
    private String bankName;
    private String bankNameCn;
    private String swiftCode;


    public Bank() {
    }

    protected Bank(Parcel in) {
        if (in.readByte() == 0) {
            bankId = null;
        } else {
            bankId = in.readLong();
        }
        bankNameEn = in.readString();
        bankName = in.readString();
        bankNameCn = in.readString();
        swiftCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (bankId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(bankId);
        }
        dest.writeString(bankNameEn);
        dest.writeString(bankName);
        dest.writeString(bankNameCn);
        dest.writeString(swiftCode);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Bank> CREATOR = new Creator<Bank>() {
        @Override
        public Bank createFromParcel(Parcel in) {
            return new Bank(in);
        }

        @Override
        public Bank[] newArray(int size) {
            return new Bank[size];
        }
    };

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public String getBankNameEn() {
        return bankNameEn;
    }

    public void setBankNameEn(String bankNameEn) {
        this.bankNameEn = bankNameEn;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankNameCn() {
        return bankNameCn;
    }

    public void setBankNameCn(String bankNameCn) {
        this.bankNameCn = bankNameCn;
    }

    public String getSwiftCode() {
        return swiftCode;
    }

    public void setSwiftCode(String swiftCode) {
        this.swiftCode = swiftCode;
    }


    @Override
    public String getKey() {
        return bankName;
    }

    @Override
    public String getValue() {
        return bankId.toString();
    }
}
