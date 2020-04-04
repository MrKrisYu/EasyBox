package com.krisyu.easybox.activity.bottom_navigation.normal.search_fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.krisyu.easybox.R;

/**
 *   还有ReceivedDelivery的搜索还未完成。
 */

public class SearchFragment extends Fragment implements View.OnClickListener{

    private Button btn_sentDelivery;
    private Button btn_receivedDeilvery;
    private SearchView searchView;

    private Fragment sentDeliveryFragment = null;
    private Fragment receivedDeliveryFragment = null;

    private Context mContext = null;
    private FragmentManager fragmentManager = null;
    private static final String TAG = "SearchFragment";




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View searchFragment = inflater.inflate(R.layout.fragment_search, container, false);
        mContext = getActivity();

//        initData();
        initView(searchFragment);
        initEvents();
        return searchFragment;
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    /**
     * 单击按钮选择 显示不同的碎片,注意需要刷新数据
     * @param v
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.applicationBar_btn_myDeliverySent:
                btn_sentDelivery.setTextColor(ContextCompat.getColor(mContext,R.color.price_highlight_red));
                btn_sentDelivery.setBackgroundResource(R.drawable.underline_button_bg);
                btn_receivedDeilvery.setTextColor(Color.BLACK);
                btn_receivedDeilvery.setBackgroundResource(R.drawable.default_button_bg);

                replaceFragment(sentDeliveryFragment, BUTTON_SENT_DELIVERY);
                break;
            case R.id.applicationBar_btn_myDeliveryReceived:
                btn_receivedDeilvery.setTextColor(ContextCompat.getColor(mContext,R.color.price_highlight_red));
                btn_receivedDeilvery.setBackgroundResource(R.drawable.underline_button_bg);
                btn_sentDelivery.setTextColor(Color.BLACK);
                btn_sentDelivery.setBackgroundResource(R.drawable.default_button_bg);
                replaceFragment(receivedDeliveryFragment, BUTTON_RECEIVED_DELIVERY);
                break;

                default:
        }
    }

    private void initView(View view){
        // 控件初始化
            // 初始化 “碎片选择”按钮
        btn_sentDelivery = (Button)view.findViewById(R.id.applicationBar_btn_myDeliverySent);
        btn_receivedDeilvery = (Button)view.findViewById(R.id.applicationBar_btn_myDeliveryReceived);
        btn_receivedDeilvery.setOnClickListener(SearchFragment.this);
        btn_sentDelivery.setOnClickListener(SearchFragment.this);
            // 初始化 SearchView
        searchView = (SearchView)view.findViewById(R.id.applicationBar_searchView);

        // 默认为 我寄的 碎片页面
        fragmentManager = getFragmentManager();
        replaceFragment(sentDeliveryFragment, BUTTON_SENT_DELIVERY);
            // 强调”我寄的“按钮
        btn_sentDelivery.setTextColor(ContextCompat.getColor(mContext,R.color.price_highlight_red));
        btn_sentDelivery.setBackgroundResource(R.drawable.underline_button_bg);

    }

    private void initEvents(){
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText){
                DeliverySentFragment sentFragment = null;
                if (getFragmentManager() != null) {
                    sentFragment = (DeliverySentFragment)fragmentManager.findFragmentByTag("DeliverySent");
                }
                if(null == sentFragment){
                    throw new NullPointerException();
                }else{
                    if(TextUtils.isEmpty(newText)){
                        sentFragment.getDeliverySentAdapter().getFilter().filter("");
                    }else{
                        sentFragment.getDeliverySentAdapter().getFilter().filter(newText);
                    }
                }

                return true;
            }

        });
    }

    /**
     *  To replace fragment by methods of add() & hide()
     * @param fragment
     */
    private static final Boolean BUTTON_SENT_DELIVERY = true;
    private static final Boolean BUTTON_RECEIVED_DELIVERY = false;

    private void replaceFragment(Fragment fragment, boolean buttonType){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(null == fragment){
            if(buttonType){
                fragment = new DeliverySentFragment();
                sentDeliveryFragment = fragment;
                transaction.add(R.id.frameLayout_for_recyclerView, fragment,"DeliverySent");
            }else{
                fragment = new DeliveryReceivedFragment();
                receivedDeliveryFragment = fragment;
                transaction.add(R.id.frameLayout_for_recyclerView, fragment, "DeliveryReceived");
            }

        }

        hideFragment(transaction);
        transaction.show(fragment).commit();
    }

    /**
     * To hide fragment had been stored
     * @param transaction
     */
    private void hideFragment(FragmentTransaction transaction){

        if(null != sentDeliveryFragment){
            transaction.hide(sentDeliveryFragment);
        }
        if(null != receivedDeliveryFragment){
            transaction.hide(receivedDeliveryFragment);
        }

    }




}
