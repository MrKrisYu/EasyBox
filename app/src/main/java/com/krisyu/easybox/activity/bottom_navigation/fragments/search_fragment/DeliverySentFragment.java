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
import com.krisyu.easybox.adapter.DeliverySentAdapter;
import com.krisyu.easybox.mode.DeliveryItem;

import java.util.ArrayList;
import java.util.List;

public class DeliverySentFragment extends Fragment {

    private RecyclerView sentDeliveryRecyclerView;
    private DeliverySentAdapter deliverySentAdapter;

    private Context mContext;

    private List<DeliveryItem> deliverySentItemDatas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View sentDeliveryFragment = inflater.inflate(R.layout.fragment_search_sent_delivery,
                container,false);

        mContext = getActivity();

        initData();
        initView(sentDeliveryFragment);

        return sentDeliveryFragment;
    }

    private void initView(View view){
        // 初始化 RecyclerView
        // “我寄的”碎片中的 RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        sentDeliveryRecyclerView = (RecyclerView)view.findViewById(R.id.sentDelivery_recyclerView);
        deliverySentAdapter = new DeliverySentAdapter(deliverySentItemDatas, mContext);
        sentDeliveryRecyclerView.setHasFixedSize(true);
        sentDeliveryRecyclerView.setLayoutManager(layoutManager);
        sentDeliveryRecyclerView.setAdapter(deliverySentAdapter);

    }

    private void initData(){
        deliverySentItemDatas = new ArrayList<>();
        deliverySentItemDatas.add(new DeliveryItem(1, 2, 22,
                "100000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliverySentItemDatas.add(new DeliveryItem(1, 3, 33,
                "200000000","2011年2月22日 20:13:29", "温州市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliverySentItemDatas.add(new DeliveryItem(1, 2, 22,
                "300000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliverySentItemDatas.add(new DeliveryItem(1, 2, 22,
                "300000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliverySentItemDatas.add(new DeliveryItem(1, 2, 22,
                "300000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliverySentItemDatas.add(new DeliveryItem(1, 2, 22,
                "100000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
        deliverySentItemDatas.add(new DeliveryItem(1, 2, 22,
                "100000000","2012年2月1日 20:13:59", "桂林市",
                "余老板","福州市","蔡经理","待揽件",
                "文件"));
    }

    public RecyclerView getSentDeliveryRecyclerView(){
        return sentDeliveryRecyclerView;
    }

    public DeliverySentAdapter getDeliverySentAdapter(){
        return deliverySentAdapter;
    }


}
