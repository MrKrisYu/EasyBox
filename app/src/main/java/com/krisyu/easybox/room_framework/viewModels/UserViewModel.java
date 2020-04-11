package com.krisyu.easybox.room_framework.viewModels;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.krisyu.easybox.room_framework.entities.UserData;
import com.krisyu.easybox.room_framework.reposity.UserRepository;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class UserViewModel extends ViewModel {

    private LiveData<List<UserData>> mAllUserData;
    private UserRepository mUserRepository;

    public LiveData<List<UserData>> getAllUserData(){
        return mAllUserData;
    }
    public UserRepository getUserRepository(){
        return mUserRepository;
    }

    public UserViewModel(@NonNull Context context){
        mUserRepository = new UserRepository(context);
        mAllUserData = mUserRepository.getAllUserDatas();

    }

  // Factory
  public static class UserInstanceFactory implements ViewModelProvider.Factory{
        private Context context;

        public UserInstanceFactory(Context context){
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                return modelClass.getConstructor(Context.class).newInstance(context);
            } catch (IllegalAccessException e){
                e.printStackTrace();
            } catch (InstantiationException e){
                e.printStackTrace();
            }catch (NoSuchMethodException e){
                e.printStackTrace();
            }catch (InvocationTargetException e){
                e.printStackTrace();
            }
            return null;
        }


  }



//-------------------------------------仓库的操作方法------------------------------------------



}
