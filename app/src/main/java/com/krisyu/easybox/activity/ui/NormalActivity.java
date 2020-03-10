package com.krisyu.easybox.activity.ui;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;


import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.krisyu.easybox.R;
import com.krisyu.easybox.activity.ui.normal.home_fragment.HomeFragment;
import com.krisyu.easybox.activity.ui.normal.location_fragment.LocationFragment;
import com.krisyu.easybox.activity.ui.normal.message_fragment.MessageFragment;
import com.krisyu.easybox.activity.ui.normal.mine_fragment.MineFragment;
import com.krisyu.easybox.activity.ui.normal.search_fragment.SearchFragment;
import com.krisyu.easybox.base.BaseActivity;
import com.next.easynavigation.view.EasyNavigationBar;

import java.util.ArrayList;
import java.util.List;


/*****************************************************
 *              Task List
 *
 *  1、 完成主页设计碎片构架----------------> 进度：完成构架，功能待完善
 *      To solve：
 *         * groupItem 不能单击，进而展开，显示childItem。
 *
 *
 *  2、 完成个人信息活动构架----------------> 进度：完成构架，功能待完善
 *      To solve：
 *       √ * 解决Fragment 滑动显示更多的内容
 *
 *
 *  3、 完成查快递构架---------------------> 进度： 完成构架，完善搜索模块，数据更新模块
 *      To solve:
 *        √ * 解决应用栏的问题
 *        √ * 搜索功能
 *
 *
 *
 *  4、 完成服务服务网点构架---------------> 进度：
 *      To solve:
 *
 *
 *  5、 完成消息对话构架------------------> 进度：
 *      To solve:
 *
 *
 *  *、注意导航栏红点和消息数的显示---------> 进度：
 *      To solve:
 *
 *
 *****************************************************/



public class NormalActivity extends BaseActivity {

    private EasyNavigationBar navigationBar;
    private String[] tabText = {"首页", "快递查询", "消息", "服务网点", "我的"};
    // 未选中的icon
    private int[] normalIcon = {R.drawable.home, R.drawable.search, R.drawable.message,
            R.drawable.location, R.drawable.mine};
    // 选中的Icon
    private  int[] selectIcon = {R.drawable.home_yes, R.drawable.search_yes, R.drawable.message_yes,
            R.drawable.location_yes, R.drawable.mine_yes};

    private List<Fragment> fragments = new ArrayList<>();


    public EasyNavigationBar getNavigationBar() {
        return navigationBar;
    }

    @Override
    protected  int getContentViewId(){
        return R.layout.navigation_tab;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        Toolbar navigationToolbar = (Toolbar)findViewById(R.id.navigation_toolbar);
//        setSupportActionBar(navigationToolbar);

        navigationBar = (EasyNavigationBar) findViewById(R.id.navigationBar);


        fragments.add(new HomeFragment());
        fragments.add(new SearchFragment());
        fragments.add(new MessageFragment());
        fragments.add(new LocationFragment());
        fragments.add(new MineFragment());

        navigationBar.titleItems(tabText)
                .normalIconItems(normalIcon)
                .selectIconItems(selectIcon)
                .iconSize(32)
                .fragmentList(fragments)
                .fragmentManager(getSupportFragmentManager())
                .canScroll(false)
                .normalTextColor(getColor(R.color.tab_black))
                .selectTextColor(getColor(R.color.tab_blue))
                .navigationBackground(getColor(R.color.tab_white))
                .build();

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.options_menu, menu);
//
//        return true;
//    }


}
