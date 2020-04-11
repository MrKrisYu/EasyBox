package com.krisyu.easybox.room_framework.entities;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "userdata_table")
public class UserData implements Serializable {

    @ColumnInfo(name = "user_id")
    private int userId;

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "user_name")
    private String userName;

    @ColumnInfo(name = "user_password")
    private String userPwd;

    public UserData(int userId, @NonNull String userName, String userPwd) {
        this.userId = userId;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    @Ignore
    public UserData(@NonNull String userName, String userPwd) {
        this.userId = -2;  // 非数据库中数据的标志
        this.userName = userName;
        this.userPwd = userPwd;
    }


    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    @Ignore
    public boolean isSameContent(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof UserData) {
            UserData anotherObject = (UserData) anObject;
            if(anotherObject.userName.equals(this.userName) && anotherObject.userPwd.equals(this.userPwd)){
                return true;
            }
        }
        return false;
    }

    @Ignore
    public boolean isEmpty(){
        return (this.getUserName() == null && this.getUserPwd() == null
                && this.getUserName().length() == 0 && this.getUserPwd().length() == 0);
    }

}
