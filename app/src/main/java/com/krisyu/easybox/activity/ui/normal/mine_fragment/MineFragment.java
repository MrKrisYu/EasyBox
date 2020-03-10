package com.krisyu.easybox.activity.ui.normal.mine_fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.krisyu.easybox.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class MineFragment extends Fragment implements View.OnClickListener {

    private View mView;
    private Context mContext = null;

    private CircleImageView headPic;
    private TextView userName;
    private TextView userDescription;
    private Button addressBook;
    private Button myPurchaseRecord;
    private Button myCollection;
    private Button myCustomerService;
    private Button about;
    private Button suggestionFeedback;
    private Button setting;
    private Button exitAccount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mView = inflater.inflate(R.layout.fragment_mine, container, false);
        mContext = getActivity();
        initView(mView);
        initEvents();


        return mView;
    }

    private void initView(View view){
        headPic = (CircleImageView)view.findViewById(R.id.fragment_mine_cirImg_headPic);
        userDescription = (TextView)view.findViewById(R.id.fragment_mine_textView_description);
        userName = (TextView)view.findViewById(R.id.fragment_mine_textView_userName);
        addressBook = (Button)view.findViewById(R.id.fragment_mine_btn_address_book);
        myPurchaseRecord = (Button)view.findViewById(R.id.fragment_mine_btn_purchase_records);
        myCollection = (Button)view.findViewById(R.id.fragment_mine_btn_myCollection);
        myCustomerService = (Button)view.findViewById(R.id.fragment_mine_btn_myCustomerService);
        about = (Button)view.findViewById(R.id.fragment_mine_btn_about);
        suggestionFeedback = (Button)view.findViewById(R.id.fragment_mine_btn_customerFeedback);
        setting = (Button)view.findViewById(R.id.fragment_mine_btn_setting);
        exitAccount = (Button)view.findViewById(R.id.fragment_mine_btn_exitAccount);
    }

    private void initEvents(){
        headPic.setOnClickListener(MineFragment.this);
        userDescription.setOnClickListener(MineFragment.this);
        addressBook.setOnClickListener(MineFragment.this);
        myPurchaseRecord.setOnClickListener(MineFragment.this);
        myCollection.setOnClickListener(MineFragment.this);
        myCustomerService.setOnClickListener(MineFragment.this);
        about.setOnClickListener(MineFragment.this);
        suggestionFeedback.setOnClickListener(MineFragment.this);
        setting.setOnClickListener(MineFragment.this);
        exitAccount.setOnClickListener(MineFragment.this);
        userName.setText("User321123");
        userDescription.setText("这个用户很懒，什么也没有留下。");
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.fragment_mine_cirImg_headPic:
                Toast.makeText(mContext, "--头像--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_mine_textView_description:
                Toast.makeText(mContext, "--用户描述--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_mine_btn_address_book:
                Toast.makeText(mContext, "--地址簿--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_mine_btn_purchase_records:
                Toast.makeText(mContext, "--我的订单--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_mine_btn_myCollection:
                Toast.makeText(mContext, "--我的收藏--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_mine_btn_myCustomerService:
                Toast.makeText(mContext, "--我的客服--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_mine_btn_about:
                Toast.makeText(mContext, "--关于--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_mine_btn_customerFeedback:
                Toast.makeText(mContext, "--意见反馈--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_mine_btn_setting:
                Toast.makeText(mContext, "--设置--", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fragment_mine_btn_exitAccount:
                Toast.makeText(mContext, "--退出账号--", Toast.LENGTH_SHORT).show();
                break;
                default:
                    break;
        }
    }

}
