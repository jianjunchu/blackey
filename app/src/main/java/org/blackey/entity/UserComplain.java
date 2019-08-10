package org.blackey.entity;

public class UserComplain {

    private Long id;
    /**
     * 投诉用户
     */
    private Long userId;
    /**
     * 投诉内容
     */
    private String content;
    /**
     * 图片说明
     */
    private String captions;
    /**
     * 状态 0:待处理 1:处理中 2:已解决
     */
    private Integer status;
    /**
     * 联系方式
     */
    private String contact;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCaptions() {
        return captions;
    }

    public void setCaptions(String captions) {
        this.captions = captions;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
