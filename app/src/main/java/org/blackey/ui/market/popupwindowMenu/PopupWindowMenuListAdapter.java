package org.blackey.ui.market.popupwindowMenu;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.blackey.R;

import java.util.ArrayList;

/**
 * Used 菜单项列表适配器
 */

public class PopupWindowMenuListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
	/**上下文*/
	private Context myContext;
	/**自定义列表项标题集合*/
	private ArrayList<PopUpMenuBean> itemList;

	/**
	 * 构造函数
	 */
	public PopupWindowMenuListAdapter(Context context, ArrayList<PopUpMenuBean> itemlist) {
		myContext = context;
		this.itemList = itemlist;
	}

	/**
	 * 获取总的条目数
	 */
	@Override
	public int getItemCount() {
		return itemList.size();
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(myContext).inflate(R.layout.popwindowmenuutil_list_item, parent, false);
		ItemViewHolder itemViewHolder = new ItemViewHolder(view);
		return itemViewHolder;
	}

	/**
	 * 声明grid列表项ViewHolder*/
	static class ItemViewHolder extends RecyclerView.ViewHolder
	{
		public ItemViewHolder(View view)
		{
			super(view);

			itemLayout = (LinearLayout) view.findViewById(R.id.listitem_layout);
			itemText = (TextView) view.findViewById(R.id.listitemText);
			itemImg = (ImageView) view.findViewById(R.id.listitemImg);
			itemLine = view.findViewById(R.id.item_line);
		}

		LinearLayout itemLayout;
		ImageView itemImg;
		TextView itemText;
		View itemLine;
	}

	/**
	 * 将数据绑定至ViewHolder
	 */
	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int index) {

		//判断属于列表项还是上拉加载区域
		if(viewHolder instanceof ItemViewHolder){
			final ItemViewHolder itemViewHold = ((ItemViewHolder)viewHolder);

			if(itemList.get(index).getImgResId() != 0){
				itemViewHold.itemImg.setImageResource(itemList.get(index).getImgResId());//赋值图标
			}else{
				itemViewHold.itemImg.setVisibility(View.GONE);//如果没有res的id值，则隐藏图标
			}

			itemViewHold.itemText.setText(itemList.get(index).getItemStr());//赋值文本

			if(index == itemList.size() - 1){
				itemViewHold.itemLine.setVisibility(View.GONE);//隐藏最后一行的底部横线
			}

			//如果设置了回调，则设置点击事件
			if (mOnItemClickLitener != null)
			{
				itemViewHold.itemLayout.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						int position = itemViewHold.getLayoutPosition();//在增加数据或者减少数据时候，position和index就不一样了
						mOnItemClickLitener.onItemClick(position);
					}
				});
			}
		}
	}

	/**
	 * 添加Item--用于动画的展现*/
	public void addItem(int position,PopUpMenuBean itemModel) {
		itemList.add(position,itemModel);
		notifyItemInserted(position);
	}
	/**
	 * 删除Item--用于动画的展现*/
	public void removeItem(int position) {
		itemList.remove(position);
		notifyItemRemoved(position);
	}

	/*=====================添加OnItemClickListener回调================================*/
	public interface OnItemClickLitener
	{
		void onItemClick(int position);
	}

	private OnItemClickLitener mOnItemClickLitener;

	public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
	{
		this.mOnItemClickLitener = mOnItemClickLitener;
	}

}
