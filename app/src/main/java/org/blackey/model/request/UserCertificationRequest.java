package org.blackey.model.request;

import android.os.Parcel;
import android.os.Parcelable;

public class UserCertificationRequest implements Parcelable {


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
    private String birthDay;
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

    public UserCertificationRequest() {
    }


    protected UserCertificationRequest(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        birthDay = in.readString();
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
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(birthDay);
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
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserCertificationRequest> CREATOR = new Creator<UserCertificationRequest>() {
        @Override
        public UserCertificationRequest createFromParcel(Parcel in) {
            return new UserCertificationRequest(in);
        }

        @Override
        public UserCertificationRequest[] newArray(int size) {
            return new UserCertificationRequest[size];
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

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
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
}
