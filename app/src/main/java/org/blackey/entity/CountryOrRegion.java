package org.blackey.entity;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * 国家或地区
 * <p/>
 * created by OuyangPeng on 2016/11/27.
 */
public class CountryOrRegion implements Parcelable {

    /**
     * 地区ID
     */
    private Long countryId;

    /**
     * 国家或地区 代号码，比如 CN、TW、HK、US
     */
    private String countryCode;

    /**
     * 国家或地区 区号
     */
    private Integer areaCode; //区号

    /**
     * 国家或地区 区号
     */
    private String showAreaCode; //区号

    // 拼音排序后需要的两个字段
    /**
     * 排序首字母
     */
    private String sortLetters;
    /**
     * 转换后的拼音名称
     */
    private String pinyinName;

    // 笔画排序后需要的两个字段

    /**
     * 国家或地区的名称，根据countryCode从资源文件获取到的不同的名称，可能是英文、简体中文、繁体中文、印尼文等
     */
    private String name;

    /**
     * 国家或地区的名称的笔画数量
     */
    private int strokeCount;


    private int  visibilityObservable;


    protected CountryOrRegion(Parcel in) {
        if (in.readByte() == 0) {
            countryId = null;
        } else {
            countryId = in.readLong();
        }
        countryCode = in.readString();
        if (in.readByte() == 0) {
            areaCode = null;
        } else {
            areaCode = in.readInt();
        }
        sortLetters = in.readString();
        pinyinName = in.readString();
        name = in.readString();
        strokeCount = in.readInt();
    }

    public static final Creator<CountryOrRegion> CREATOR = new Creator<CountryOrRegion>() {
        @Override
        public CountryOrRegion createFromParcel(Parcel in) {
            return new CountryOrRegion(in);
        }

        @Override
        public CountryOrRegion[] newArray(int size) {
            return new CountryOrRegion[size];
        }
    };

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

    public Integer getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(Integer areaCode) {
        this.areaCode = areaCode;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getPinyinName() {
        return pinyinName;
    }

    public void setPinyinName(String pinyinName) {
        this.pinyinName = pinyinName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStrokeCount() {
        return strokeCount;
    }

    public void setStrokeCount(int strokeCount) {
        this.strokeCount = strokeCount;
    }

    @Override
    public String toString() {
        return "CountryOrRegion{" +
                "countryId=" + countryId +
                ", countryCode='" + countryCode + '\'' +
                ", areaCode=" + areaCode +
                ", sortLetters='" + sortLetters + '\'' +
                ", pinyinName='" + pinyinName + '\'' +
                ", name='" + name + '\'' +
                ", strokeCount='" + strokeCount + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(countryId);
        dest.writeString(countryCode);
        dest.writeInt(areaCode);
        dest.writeString(sortLetters);
        dest.writeString(pinyinName);
        dest.writeString(name);
        dest.writeInt(strokeCount);
    }
    public CountryOrRegion() {
    }

    public String getShowAreaCode() {
        return showAreaCode;
    }

    public void setShowAreaCode(String showAreaCode) {
        this.showAreaCode = showAreaCode;
    }

    public int getVisibilityObservable() {
        return visibilityObservable;
    }

    public void setVisibilityObservable(int visibilityObservable) {
        this.visibilityObservable = visibilityObservable;
    }
}
