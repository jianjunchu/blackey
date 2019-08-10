package org.blackey.ui.market.popupwindowMenu;

/**
 * Used popupWindow 的menu列表项的bean类
 */

public class PopUpMenuBean {
	private int ImgResId;//图片资源id值，如果没有图标，则赋值0
	private String itemStr;//列表项名称

	public PopUpMenuBean(){
		ImgResId = 0;//默认值为0
	}

	public int getImgResId() {
		return ImgResId;
	}

	public void setImgResId(int imgResId) {
		ImgResId = imgResId;
	}

	public String getItemStr() {
		return itemStr;
	}

	public void setItemStr(String itemStr) {
		this.itemStr = itemStr;
	}
}
