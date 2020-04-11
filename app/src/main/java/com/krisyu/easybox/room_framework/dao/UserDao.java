package com.krisyu.easybox.room_framework.dao;


import androidx.lifecycle.LiveData;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.krisyu.easybox.room_framework.entities.UserData;

import java.util.List;

@Dao
public interface UserDao {

    // 插入一组数据
    @Insert
    long[] insertUserDatas(UserData...userData);

    // 通过用户ID 获取用户名
    @Query("select user_name from userdata_table where user_id like :id limit 1")
    String fetchUserNameByID(int id);

    // 通过用户名 获取用户名
    @Query("select user_name from userdata_table where user_name like :userName limit 1")
    String fetchUserNameByUserName(String userName);


    //通过用户名 获取用户的所有数据
    @Query("select * from userdata_table where user_name like :userName limit 1")
    UserData fetchUserDataByName(String userName);

    //通过用户名和密码 获取用户的所有数据
    @Query("select * from userdata_table where user_name like :userName "
    + "and user_password like :userPwd limit 1")
    UserData fetchUserDataByNameAndPwd(String userName, String userPwd);

    //通过用户名ID 获取用户的所有数据
    @Query("select * from userdata_table where user_id like :id")
    UserData fetchUserDataById(int id);

    //获取用户的所有数据并以姓名为条件进行升序排序
    @Query("select * from userdata_table order by user_name asc")
    LiveData<List<UserData>> fetchAllUserData();

    //删除一组用户的所有数据
    @Delete
    int deleteUsers(UserData...userData);

    //  删除指定用户ID的所有数据
    @Query("delete from userdata_table where user_id like :userId")
    int deleteUserDataByID(int userId);

    //  删除指定用户名 的所有数据
    @Query("delete from userdata_table where user_name like :userName")
    int deleteUserDataByName(String userName);

    //  删除所有用户数据
    @Query("delete from userdata_table")
    int deleteAllUserData();

    //  更新一组用户的数据
    @Update
    int updateUsers(UserData...userData);

    //  更新匹配用户名和用户密码 用户的数据
    @Query("update userdata_table set user_password = :newUserPwd " +
            "where user_name like :oldUserName AND user_password like :oldUserPwd")
    int updateUserPwdByNameAndPwd(String oldUserName, String oldUserPwd, String newUserPwd);

    //  更新匹配用户名的 用户的数据
    @Query("update userdata_table set user_password = :newUserPwd " +
            "where user_name like :oldUserName")
    int updateUserPwdByName(String oldUserName, String newUserPwd);

}
