package com.krisyu.easybox.room_framework.reposity;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.krisyu.easybox.room_framework.dao.UserDao;
import com.krisyu.easybox.room_framework.entities.UserData;

@Database(entities = {UserData.class}, version = 2, exportSchema = false)
public abstract class UserRoomDatabase extends RoomDatabase {
    private static volatile UserRoomDatabase mUserRoomDatabase;
    private static String ROOMDATABASENAME = "user_database";

    /**
     * 获取单例数据库
     * @param context 上下文
     * @return Room数据库实例
     */
    public static UserRoomDatabase getInstance(Context context){
        if(null == mUserRoomDatabase){
            synchronized (UserRoomDatabase.class){
                if(null == mUserRoomDatabase){
                    mUserRoomDatabase = Room.databaseBuilder(context.getApplicationContext(), UserRoomDatabase.class, ROOMDATABASENAME)
                                            .fallbackToDestructiveMigration()
                                            .build();
                }
            }
        }
        return mUserRoomDatabase;
    }



//---------------------------------------DAO接口--------------------------------------------
    public abstract UserDao userDao();

}
