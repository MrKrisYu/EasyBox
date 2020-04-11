package com.krisyu.easybox.room_framework.reposity;

import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;

import androidx.lifecycle.LiveData;

import com.krisyu.easybox.room_framework.dao.UserDao;
import com.krisyu.easybox.room_framework.entities.UserData;
import com.krisyu.easybox.utils.LogUtil;

import java.util.List;

public class UserRepository {
    // 数据来源标志位
    public static final int DATASOURCE_FROM_ROOM = 0x10000000;
    public static final int DATASOURCE_FROM_WEBSOCKET = 0x01000000;

    private static final String TAG = "UserRepository";
    private UserDao mUserDao;
    private LiveData<List<UserData>> mUserData;
    private static UserRoomDatabase db;


    // 构造方法
    public UserRepository(Context context){
        db = UserRoomDatabase.getInstance(context);
        mUserDao = db.userDao();
        mUserData = mUserDao.fetchAllUserData();
    }

    public static void closeDatabase(){
        if(db != null){
            db.close();
            db = null;
        }
    }

//-------------------------------------RoomDatabase 相关标志位------------------------------------------


    // 约束条件的标准值
    public static final String SELECTION_USERNAME = "user_name";
    public static final String SELECTION_USERID = "user_ID";
    public static final String SELECTION_USERPWD = "user_password";

    // 更新字段的标准值
    public static final String UPDATE_FIELD_NEW_USERNAME = "new_userName";
    public static final String UPDATE_FIELD_NEW_USERPWD = "new_userPwd";

    //-----------------------------------token-------------------------------------------
    // query 的方法标志
    // 每二十个 返回值类型不同，如前二十个返回值类型为UserData;接下来二十给放回值类型为String
    public static final int FETCH_USERDATA_BY_NAME = 0;
    public static final int FETCH_USERDATA_BY_ID = 1;
    public static final int FETCH_USERDATA_BY_NAME_AND_PWD = 2;
    public static final int FETCH_USERNAME_BY_ID = 20;
    public static final int FETCH_USERNAME_BY_NAME = 21;

    // update 的方法标志
    public static final int UPDATE_USERS = 101;
    public static final int UPDATE_USERPWD_BY_NAME = 102;
    public static final int UPDATE_USERPWD_BY_NAME_AND_PWD = 103;

    // delete的方法标志
    public static final int DELETE_ALL_USERS = 201;
    public static final int DELETE_USERS = 202;
    public static final int DELETE_BY_NAME = 203;

    // insert的方法标志
    public static final int INSERT_USERDATAS = 301;

//--------------------------------------RoomDatabase的 CRUD方法封装---------------------------------------------

    /**
     * RoomDatabase的 Query方法封装
     * @param token 方法标志位，具体的操作的标志位。使用UserRepository内的标志位
     * @param selection 约束条件，使用UserRepository内的标志位：SELECTION_USERNAME、SELECTION_USERID、
     *                  SELECTION_USERPWD.
     * @param selectionArgs 约束条件的具体值，注意和约束条件顺序一致
     * @return 查询到的Object。如果要使用该Object 应该使用强制类型转换
     *  如果查询到的值为空或者是方法标志位不对，则返回null
     */
    public Object query(int token, String[] selection, String[] selectionArgs){
        LogUtil.e(TAG, "query: token = " + token);
        ContentValues selectionContent = null;
        if(selection != null && selectionArgs != null){
            selectionContent = selectionFiller(selection, selectionArgs);
        }
        final Object result;
        switch (token){
            case FETCH_USERDATA_BY_NAME:
                if(selectionContent != null){
                    result = mUserDao.fetchUserDataByName(selectionContent.getAsString(SELECTION_USERNAME));
                }else{
                    result = null;
                }
                break;
            case FETCH_USERDATA_BY_ID:
                if(selectionContent != null){
                    result = mUserDao.fetchUserDataById(selectionContent.getAsInteger(SELECTION_USERID));
                }else{
                    result = null;
                }
                break;
            case FETCH_USERDATA_BY_NAME_AND_PWD:
                if(selectionContent != null){
                    result = mUserDao.fetchUserDataByNameAndPwd(selectionContent.getAsString(SELECTION_USERNAME),
                            selectionContent.getAsString(SELECTION_USERPWD));
                }else{
                    result = null;
                }
                break;
            case FETCH_USERNAME_BY_ID:
                if(selectionContent != null){
                    result = mUserDao.fetchUserNameByID(selectionContent.getAsInteger(SELECTION_USERID));
                }else{
                    result = null;
                }
                break;
            case FETCH_USERNAME_BY_NAME:
                if(selectionContent != null){
                    result = mUserDao.fetchUserNameByUserName(selectionContent.getAsString(SELECTION_USERNAME));
                }else{
                    result = null;
                }
                break;
            default:
                result = null;
        }
        LogUtil.d(TAG, "query Result = " + result);
        return result;
    }

    /**
     * RoomDatabase的 Insert方法封装
     * @param token 方法标志位，具体的操作的标志位。使用UserRepository内的标志位
     * @param userData  插入的用户数据
     * @return 返回被插入的行数值
     */
    public long insert(int token, UserData...userData){
        LogUtil.e(TAG, "insert: token = " + token);
        LogUtil.e(TAG, "insert: userData.ID = " + userData[0].getUserId());
        long[] rows = mUserDao.insertUserDatas(userData);
        return rows[rows.length - 1];
    }

    /**
     * RoomDatabase 的update封装方法
     * @param token 方法标志位，具体的操作的标志位。使用UserRepository内的标志位
     * @param selection 约束条件，使用UserRepository内的标志位：SELECTION_USERNAME、SELECTION_USERID、
     *                 SELECTION_USERPWD.
     * @param selectionArgs 约束条件的具体值，注意和约束条件顺序一致
     * @param updateFields 需要更新的字段名，使用UserRepository内的标志位:UPDATE_FIELD_NEW_USERNAME、
     *                     UPDATE_FIELD_NEW_USERPWD。
     * @param updateValues 更新字段的具体值，注意和字段名顺序一致
     * @param userData 如果userData不为null，则selection、selectionArgs、updateFields、updateValues
     *                 要直接为null,表明更新方式为整个实体进行更新，其中检索方式为主键检索；
     *                 如果userData为空，则其他四项应不为空
     * @return 查询成功，则返回更新的那条记录的行数（需要验证）；查询失败则返回-44.（待完善）
     */
    public int update(int token, String[] selection, String[] selectionArgs, String[] updateFields,
            String[] updateValues, UserData...userData){
        LogUtil.e(TAG, "update: token = " + token);
        ContentValues selecContent = null;
        ContentValues updateContent = null;
        if(selection != null && selectionArgs != null){
            selecContent = selectionFiller(selection, selectionArgs);
        }
        if(updateFields != null && updateValues != null){
            updateContent = updateConentFiller(updateFields, updateValues);
        }
        switch (token){
            case UPDATE_USERS:
                return mUserDao.updateUsers(userData);
            case UPDATE_USERPWD_BY_NAME:
                if(selecContent != null && updateContent != null){
                    return mUserDao.updateUserPwdByName(selecContent.getAsString(SELECTION_USERNAME),
                            updateContent.getAsString(UPDATE_FIELD_NEW_USERPWD));
                }else{
                    return -44;
                }
            case UPDATE_USERPWD_BY_NAME_AND_PWD:
                if(selecContent != null && updateContent != null){
                    return mUserDao.updateUserPwdByNameAndPwd(selecContent.getAsString(SELECTION_USERNAME),
                            selecContent.getAsString(SELECTION_USERPWD),
                            updateContent.getAsString(UPDATE_FIELD_NEW_USERPWD));
                }else{
                    return -44;
                }
            default:
                return -44;
        }
    }

    /**
     * RoomDatabase 的delete封装方法
     * @param token 方法标志位，具体的操作的标志位。使用UserRepository内的标志位
     * @param selection 约束条件，使用UserRepository内的标志位：SELECTION_USERNAME、SELECTION_USERID、
     *                 SELECTION_USERPWD.
     * @param selectionArgs 约束条件的具体值，注意和约束条件顺序一致
     * @param userData 如果userData不为null，则selection、selectionArgs要为null,表明删除方式为实体删除，
     *                 其中检索方式为主键检索；如果userData为空，则其他两项应不为空。
     * @return 删除成功，则返回被删除的行数（待验证）；删除失败，则返回-44.（待完善）
     */
    public int delete(int token, String[] selection, String[] selectionArgs, UserData...userData){
        LogUtil.e(TAG, "delete: token = " + token);
        ContentValues selecContent = null;
        if(selection != null && selectionArgs != null){
            selecContent = selectionFiller(selection, selectionArgs);
        }
        switch (token){
            case DELETE_ALL_USERS:
                return mUserDao.deleteAllUserData();
            case DELETE_USERS:
                return mUserDao.deleteUsers(userData);
            case  DELETE_BY_NAME:
                if(selecContent != null){
                    return mUserDao.deleteUserDataByName(selecContent.getAsString(SELECTION_USERNAME));
                }else{
                    return -44;
                }
            default:
                return -44;
        }
    }




    private ContentValues selectionFiller(String[] selection, String[] selectionArgs){
        ContentValues result = new ContentValues();
        for(int i=0; i<selection.length; i++){
            result.put(selection[i], selectionArgs[i].trim());
        }

        return result;
    }

    private ContentValues updateConentFiller(String[] selection, String[] selectionArgs){
        ContentValues result = new ContentValues();
        int count = -1;
        for(int i=0; i<selection.length; i++){
            result.put(selection[i], selectionArgs[i]);
        }

        return result;
    }


//---------------------------------------Setter&Getter---------------------------------------------



    public LiveData<List<UserData>> getAllUserDatas() {
        return mUserData;
    }


//--------------------------------------------异步查询-----------------------------------------------


//***********************************AsyncQueryHandler机制***************************************



}
