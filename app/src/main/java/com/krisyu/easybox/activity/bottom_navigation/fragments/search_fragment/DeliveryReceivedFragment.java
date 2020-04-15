package com.krisyu.easybox.activity.bottom_navigation.fragments.search_fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krisyu.easybox.R;
import com.krisyu.easybox.adapter.DeliveryReceivedAdapter;
import com.krisyu.easybox.mode.DeliveryItem;

import java.util.ArrayList;
import java.util.List;

public class DeliveryReceivedFragment extends Fragment {

    private RecyclerView deliveryReceivedRecyclerView;
    private DeliveryReceivedAdapter deliveryReceicedAdapter;

    private Context mContext;

    private List<DeliveryItem> deliveryReceivedItemDatas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View receivedDeliveryFragment = inflater.inflate(R.layout.fragment_search_received_delivery,
                container, false);

        mContext = getActivity();
        initData();
        initView(receivedDeliveryFragment);

        return receivedDeliveryFragment;
    }

    private void initData(){
        deliveryReceivedItemDatas = new ArrayList<>();
        deliveryReceivedItemDatas.add(new DeliveryItem(1, 2, 22,
                "100000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliveryReceivedItemDatas.add(new DeliveryItem(1, 3, 33,
                "200000000","2011年2月22日 20:13:29", "温州市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliveryReceivedItemDatas.add(new DeliveryItem(1, 2, 22,
                "300000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliveryReceivedItemDatas.add(new DeliveryItem(1, 2, 22,
                "300000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliveryReceivedItemDatas.add(new DeliveryItem(1, 2, 22,
                "300000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliveryReceivedItemDatas.add(new DeliveryItem(1, 2, 22,
                "100000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliveryReceivedItemDatas.add(new DeliveryItem(1, 2, 22,
                "100000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
    }

    private void initView(View view){
        // 初始化 RecyclerView
        // “我寄的”碎片中的 RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        deliveryReceivedRecyclerView = (RecyclerView)view.findViewById(R.id.receivedDelivery_recyclerView);
        deliveryReceicedAdapter = new DeliveryReceivedAdapter(deliveryReceivedItemDatas, mContext);
        deliveryReceivedRecyclerView.setHasFixedSize(true);
        deliveryReceivedRecyclerView.setLayoutManager(layoutManager);
        deliveryReceivedRecyclerView.setAdapter(deliveryReceicedAdapter);
    }

}
