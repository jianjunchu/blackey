package org.blackey.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class UserCertification implements Parcelable {

    public static  final int IDENTITY_CARD_TYPE_PASSPORT =1;
    public static  final int IDENTITY_CARD_TYPE_DOMESTIC_IDENTITY_CARD =2;

    public static  final int PASSED_CHECK_PENDING = 0;
    public static  final int PASSED_FAILED = 1;
    public static  final int PASSED_APPROVE = 2;


    /**
     * 姓
     */
    private String firstName;
    /**
     * 名
     */
    private String lastName;
    /**
     * 出生日期
     */
    private Date birthDay;
    /**
     * 证件类型：  1 Passport,   2 Domestic Identity Card
     */
    private Integer identityCardType;

    /**
     * 国籍
     */
    private Long nationality;
    /**
     * 证件号码
     */
    private String identityNumber;
    /**
     * 正面照片
     */
    private String identityCardFullFace;
    /**
     * 反面照片
     */
    private String identityCardReverseFace;
    /**
     * 照片合照
     */
    private String identityCardGroupPhoto;
    /**
     * 是否通过审核 1：否 0：待审核 2:审核通过
     */
    private Integer passed;
    /**
     * 审核备注
     */
    private String remark;

    public UserCertification() {
    }

    protected UserCertification(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        if (in.readByte() == 0) {
            identityCardType = null;
        } else {
            identityCardType = in.readInt();
        }
        if (in.readByte() == 0) {
            nationality = null;
        } else {
            nationality = in.readLong();
        }
        identityNumber = in.readString();
        identityCardFullFace = in.readString();
        identityCardReverseFace = in.readString();
        identityCardGroupPhoto = in.readString();
        if (in.readByte() == 0) {
            passed = null;
        } else {
            passed = in.readInt();
        }
        remark = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        if (identityCardType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(identityCardType);
        }
        if (nationality == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(nationality);
        }
        dest.writeString(identityNumber);
        dest.writeString(identityCardFullFace);
        dest.writeString(identityCardReverseFace);
        dest.writeString(identityCardGroupPhoto);
        if (passed == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(passed);
        }
        dest.writeString(remark);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserCertification> CREATOR = new Creator<UserCertification>() {
        @Override
        public UserCertification createFromParcel(Parcel in) {
            return new UserCertification(in);
        }

        @Override
        public UserCertification[] newArray(int size) {
            return new UserCertification[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    public Integer getIdentityCardType() {
        return identityCardType;
    }

    public void setIdentityCardType(Integer identityCardType) {
        this.identityCardType = identityCardType;
    }

    public Long getNationality() {
        return nationality;
    }

    public void setNationality(Long nationality) {
        this.nationality = nationality;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getIdentityCardFullFace() {
        return identityCardFullFace;
    }

    public void setIdentityCardFullFace(String identityCardFullFace) {
        this.identityCardFullFace = identityCardFullFace;
    }

    public String getIdentityCardReverseFace() {
        return identityCardReverseFace;
    }

    public void setIdentityCardReverseFace(String identityCardReverseFace) {
        this.identityCardReverseFace = identityCardReverseFace;
    }

    public String getIdentityCardGroupPhoto() {
        return identityCardGroupPhoto;
    }

    public void setIdentityCardGroupPhoto(String identityCardGroupPhoto) {
        this.identityCardGroupPhoto = identityCardGroupPhoto;
    }

    public Integer getPassed() {
        return passed;
    }

    public void setPassed(Integer passed) {
        this.passed = passed;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
