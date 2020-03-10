package com.krisyu.easybox.mode;

import java.util.List;

public class MachineBean {

    public int code = 0;
    public List<Data> data;
    public String msg = "";

    public class Data{
        public String customMessageId = "Statu: Initial";
        public String machineType;      // 0--> 空闲； 1-->占用； 2-->停用
        public String H_val = "";
        public String T_val = "";
        public String deviceId;
        public String timestamp = "";
        public String OpenStatus = "0";  // 柜子开启与否的状态标志,0-->在线关， 1-->在线开（默认为关）,-1-->默认关
    }

}
