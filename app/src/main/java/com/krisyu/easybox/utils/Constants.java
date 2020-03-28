package com.krisyu.easybox.utils;

public interface Constants {

    //url
    String URL="http://krisyu.nat300.top/machine_mqtt/";      // 101.200.126.182
    String GET_MACHINE = "apiController/getMachine"; //获取柜子
    String OPEN_DOOR = "apiController/getMachine"; //开
    String[] arrayDeviceID = new String[]{"21700821","21700967","21700139","21700859"};   // 终端ID号码
    int NumTerminal = 4;
    String WEBSOCKET_URL_TEST = "ws://echo.websocket.org";
    String WEBSOCKET_URL = "ws://socket.visualdust.com:10000";

}
