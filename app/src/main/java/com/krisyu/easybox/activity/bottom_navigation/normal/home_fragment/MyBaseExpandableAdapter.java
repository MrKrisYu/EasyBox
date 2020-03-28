package com.krisyu.easybox.activity.bottom_navigation.normal.home_fragment;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.krisyu.easybox.R;

import java.util.List;

public class MyBaseExpandableAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<String> groupTitle;
    private SparseArray<List<ChildItem>> childMap;
    private Switch childSwitch;

    public MyBaseExpandableAdapter(Context context, List<String> groupTitle, SparseArray<List<ChildItem>> childMap){
        this.childMap = childMap;
        this.mContext = context;
        this.groupTitle = groupTitle;
    }

    // 视图类 Holder
    private class GroupHolder{
        ImageView groupIndicatorImg;
        ImageView groupExpandImg;
        TextView groupText;
    }
    private class ChildHolder{
        TextView boxSequence;
        TextView boxStatus;
        TextView boxBindStatus;
        ImageView boxImg;
        Switch boxSwitch;
    }



    // 重写 ExpandlistView接口的 方法
        // child 部分
    @Override
    public Object getChild(int groupPosition, int childPosition){
        // 返回每一个Item的编号
        return childMap.get(groupPosition).get(childPosition).getBoxSequence();
    }
    @Override
    public long getChildId(int groupPosition, int childPosition){
        return childPosition;
    }
    @Override
    public int getChildrenCount(int groupPosition){
        return childMap.get(groupPosition).size();
    }
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent){
        ChildHolder childHolder = null;
        // 缓存检测
        if(convertView == null){
            childHolder = new ChildHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_home_el_childitem, null);
            childHolder.boxSequence = (TextView)convertView.findViewById(R.id.fragment_home_el_childitem_tv_box_sequence);
            childHolder.boxStatus = (TextView)convertView.findViewById(R.id.fragment_home_el_childitem_tv_box_status);
            childHolder.boxBindStatus = (TextView)convertView.findViewById(R.id.fragment_home_el_childitem_tv_status_of_bind);
            childHolder.boxImg = (ImageView)convertView.findViewById(R.id.fragment_home_el_childitem_imageView_box);
            childHolder.boxSwitch = (Switch)convertView.findViewById(R.id.fragment_home_el_childitem_rb_switch);

            // 设置Tag
            convertView.setTag(childHolder);
        }else{
            childHolder = (ChildHolder)convertView.getTag();
        }

        // 给视图控件赋值
        childHolder.boxSequence.setText("编号： "+childMap.get(groupPosition).get(childPosition).getBoxSequence());

        boolean bindStatus = childMap.get(groupPosition).get(childPosition).isStatusOfBind();
        if(bindStatus){
            childHolder.boxBindStatus.setText("解除绑定");
        }else{
            childHolder.boxBindStatus.setText("未绑定");
        }

        int boxStatus = childMap.get(groupPosition).get(childPosition).getBoxStatus();
        if(boxStatus == ChildItem.BOX_STATUS_FREE){
            childHolder.boxStatus.setText("快递箱状态： 空闲");
            childHolder.boxImg.setBackgroundResource(R.drawable.box_free);
        }else if (boxStatus == ChildItem.BOX_STATUS_BUSY){
            childHolder.boxStatus.setText("快递箱状态： 已被占用");
            childHolder.boxImg.setBackgroundResource(R.drawable.box_occupied);
        }else if(boxStatus == ChildItem.BOX_STATUS_DISCARD){
            childHolder.boxStatus.setText("快递箱状态： 停用");
            childHolder.boxImg.setBackgroundResource(R.drawable.box_discard);
        }else{
            childHolder.boxStatus.setText("");
            childHolder.boxImg.setBackgroundResource(R.drawable.box_discard);
        }

        childHolder.boxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if(isChecked){
                    Toast.makeText(mContext, "打开柜子", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return convertView;
    }
        // group 部分
    @Override
    public Object getGroup(int groupPosition){
        return groupTitle.get(groupPosition);
    }
    @Override
    public long getGroupId(int groupPosition){
        return groupPosition;
    }
    @Override
    public int getGroupCount(){
        return groupTitle.size();
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent){
        GroupHolder groupHolder = null;
        //缓存检测
        if(null == convertView){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_home_el_groupitem, null);
            groupHolder = new GroupHolder();
            groupHolder.groupText = (TextView)convertView.findViewById(R.id.tv_group_text);
            groupHolder.groupIndicatorImg = (ImageView)convertView.findViewById(R.id.img_group_indicator);
            groupHolder.groupExpandImg = (ImageView)convertView.findViewById(R.id.img_group_expand);
            convertView.setTag(groupHolder);
        }else{
            groupHolder = (GroupHolder)convertView.getTag();
        }

        // 展开检测
        if(isExpanded){
            groupHolder.groupExpandImg.setBackgroundResource(R.drawable.down);
        }else{
            groupHolder.groupExpandImg.setBackgroundResource(R.drawable.right);
        }

        // 给组视图控件赋值
        groupHolder.groupIndicatorImg.setBackgroundResource(R.drawable.my_device);
        groupHolder.groupText.setText("我的设备" +getChildrenCount(groupPosition) + "个" );

        return convertView;
    }


        // 其他方法
//    /**
//     *  该方法根据子条目ID和分组条目ID，返回唯一的识别ID。另外，如果
//     *  hasStableIDs() 为真，该函数返回的ID必须是固定不变的。
//     * @param groupId
//     * @param childId
//     * @return
//     */
//    @Override
//    public long getCombinedChildId(long groupId, long childId){
//        return 0;
//    }
//
    /**
     * 指定分组视图及其子视图的ID对应的后台数据方法改变是，是否保持该ID
     * @return 返回是否相同的ID总是指向同一个对象
     */
    @Override
    public boolean hasStableIds(){
        return true;
    }

    /**
     * 指定位置子视图是否可选择
     * @param groupPosition 组视图位置
     * @param childPosition 子视图位置
     * @return 返回 是否可选择
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition){
        return true;
    }


}

