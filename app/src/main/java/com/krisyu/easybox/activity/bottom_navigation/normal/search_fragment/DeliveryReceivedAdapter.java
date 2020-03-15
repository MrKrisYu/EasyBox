package com.krisyu.easybox.activity.bottom_navigation.normal.search_fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;


import com.krisyu.easybox.R;

import java.util.List;

public class DeliveryReceivedAdapter extends RecyclerView.Adapter<DeliveryReceivedAdapter.DeliveryReceivedViewHolder>
    implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private List<DeliveryItem> deliveryItemList;
    private Context mContext;
    private static final String TAG = "DeliveryReceivedAdapter";



    public DeliveryReceivedAdapter(List<DeliveryItem> deliveryItems, Context context){
        this.deliveryItemList = deliveryItems;
        this.mContext = context;
    }

    // Holder类
    static class DeliveryReceivedViewHolder extends RecyclerView.ViewHolder{
        TextView boxSequence;
        TextView trackingNum;
        TextView orderTime;
        TextView senderLocation;
        TextView senderName;
        TextView receiverLocation;
        TextView receiverName;
        TextView deliveryStatus;
        TextView deliveryInfo;
        TextView deliveryPrice;
        ImageView directionPic;
        Button cancelOrder;
        Button checkLogistics;
        Button popmenu;

        public DeliveryReceivedViewHolder(final View itemView){
            super(itemView);
            boxSequence = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_textView_BoxId);
            trackingNum = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_trackingNum);
            orderTime = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_time);
            senderLocation = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_srcLocation);
            senderName = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_senderName);
            receiverLocation = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_desLocation);
            receiverName = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_receiverName);
            deliveryStatus = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_deliveryStatus);
            deliveryInfo = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_thingsInfo);
            deliveryPrice = (TextView)itemView.findViewById(R.id.receivedDeliveryItem_price);
            directionPic = (ImageView) itemView.findViewById(R.id.receivedDeliveryItem_arrowHead);
            cancelOrder = (Button)itemView.findViewById(R.id.receivedDeliveryItem_btn_confirmReceived);
            checkLogistics = (Button)itemView.findViewById(R.id.receivedDeliveryItem_btn_trackLogistics);
            popmenu = (Button)itemView.findViewById(R.id.receivedDeliveryItem_btn_more);

        }

    }

    // 重写RecyclerView.Adapter的三个方法
    @Override
    public DeliveryReceivedAdapter.DeliveryReceivedViewHolder onCreateViewHolder(ViewGroup container, int viewType){
        return new DeliveryReceivedAdapter.DeliveryReceivedViewHolder(LayoutInflater.from(mContext).inflate
                (R.layout.fragment_search_received_delivery_item, container,false));
    }
    @Override
    public void onBindViewHolder(DeliveryReceivedAdapter.DeliveryReceivedViewHolder myViewHolder, int position){
        final int i = position;
        myViewHolder.boxSequence.setText(deliveryItemList.get(i).getBoxIdwithTitle());
        myViewHolder.trackingNum.setText(deliveryItemList.get(i).getTrackingNumwithTitle());
        myViewHolder.orderTime.setText(deliveryItemList.get(i).getOrderTimewithTitle());
        myViewHolder.senderLocation.setText(deliveryItemList.get(i).getSenderLocation());
        myViewHolder.senderName.setText(deliveryItemList.get(i).getSenderName());
        myViewHolder.receiverLocation.setText(deliveryItemList.get(i).getReceiverLocation());
        myViewHolder.receiverName.setText(deliveryItemList.get(i).getReceiverName());
//        myViewHolder.directionPic.setBackgroundResource();
        myViewHolder.deliveryStatus.setText(deliveryItemList.get(i).getDeliveryStatus());
        myViewHolder.deliveryInfo.setText(deliveryItemList.get(i).getDeliveryInfo());
        myViewHolder.deliveryPrice.setText(deliveryItemList.get(i).getDeliveryPricewithTitle());

        myViewHolder.cancelOrder.setOnClickListener(this);
        myViewHolder.checkLogistics.setOnClickListener(this);
        myViewHolder.popmenu.setOnClickListener(this);
    }
    @Override
    public int getItemCount(){
        return deliveryItemList.size();
    }

    // 重写两个接口的方法
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.receivedDeliveryItem_btn_confirmReceived:
                Toast.makeText(mContext, "您单击了确认收货", Toast.LENGTH_SHORT).show();
                break;
            case R.id.receivedDeliveryItem_btn_trackLogistics:
                Toast.makeText(mContext, "您单击了查看物流",Toast.LENGTH_SHORT).show();
                break;
            case R.id.receivedDeliveryItem_btn_more:
                // 创建弹出式菜单对象
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                // 获取菜单填充器
                MenuInflater inflater = popupMenu.getMenuInflater();
                // 填充菜单
                inflater.inflate(R.menu.delivery_item_menu, popupMenu.getMenu());
                // 绑定菜单项的点击事件
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
                break;
            default:
                Toast.makeText(mContext, "DeliverySentAdapter的onClick（）: Button ID 非法",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
    @Override
    public boolean onMenuItemClick(MenuItem item){
        switch (item.getItemId()){
            case R.id.delete:
                Toast.makeText(mContext, "您单击了删除订单信息", Toast.LENGTH_SHORT).show();
                break;
            case R.id.more:
                Toast.makeText(mContext, "您单击了待添加", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(mContext, "非法 MenuItemID", Toast.LENGTH_SHORT).show();
                break;
        }
        return false;
    }


}
