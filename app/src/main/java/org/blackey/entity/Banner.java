package org.blackey.entity;

public class Banner {

    /**
     * 图片
     */
    private String bannerImg;
    /**
     * 标题
     */
    private String title;
    /**
     * 跳转链接
     */
    private String link;

    public String getBannerImg() {
        return bannerImg;
    }

    public void setBannerImg(String bannerImg) {
        this.bannerImg = bannerImg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
