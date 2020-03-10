package com.krisyu.easybox.activity.ui.normal.home_fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.krisyu.easybox.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "HomeFragment";
    private Context mContext = null;
    private ExpandableListView expandList;
    private List<String> groupData;
    private SparseArray<List<ChildItem>> childData;
    private MyBaseExpandableAdapter myAdapter;

    private Button fastExpress;
    private Button receipt;
    private Button address_book;
    private Button change_station;
    private Button more;

    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View fragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();
        initDatas();
        initView(fragmentView);
        initEvent();

        return fragmentView;
    }

    private void initDatas(){
        groupData = new ArrayList<>();
        groupData.add("---GroupTitle设备---");

        List<ChildItem> childItems = new ArrayList<>();
        childItems.add(new ChildItem("1001",ChildItem.BOX_STATUS_FREE,true));
        childItems.add(new ChildItem("1002",ChildItem.BOX_STATUS_BUSY,false));
        childItems.add(new ChildItem("1003",ChildItem.BOX_STATUS_DISCARD,true));
        childItems.add(new ChildItem("1004",ChildItem.BOX_STATUS_FREE,false));

        childData = new SparseArray<>();
        childData.put(0, childItems);

        myAdapter = new MyBaseExpandableAdapter(mContext, groupData, childData);
    }

    private void initView(View view){
        expandList = (ExpandableListView)view.findViewById(R.id.fragment_home_expandlist);

        // 这里不显示四通默认的group indicator
        expandList.setGroupIndicator(null);
        expandList.setAdapter(myAdapter);

        fastExpress = (Button)view.findViewById(R.id.btn_fast_express);
        receipt = (Button)view.findViewById(R.id.btn_receipt);
        address_book = (Button)view.findViewById(R.id.btn_address_book);
        change_station = (Button)view.findViewById(R.id.btn_change_station);
        more = (Button)view.findViewById(R.id.btn_more);
    }

    private void initEvent(){
        fastExpress.setOnClickListener(HomeFragment.this);
        receipt.setOnClickListener(HomeFragment.this);
        address_book.setOnClickListener(HomeFragment.this);
        change_station.setOnClickListener(HomeFragment.this);
        more.setOnClickListener(HomeFragment.this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_fast_express:
                Toast.makeText(mContext, "--快速寄件--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_receipt:
                Toast.makeText(mContext, "--取件--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_address_book:
                Toast.makeText(mContext, "--地址簿--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_change_station:
                Toast.makeText(mContext, "--修改基站--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_more:
                Toast.makeText(mContext, "--更多--", Toast.LENGTH_SHORT).show();
                break;
                default:break;
        }
    }

}
