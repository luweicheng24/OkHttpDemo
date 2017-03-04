package com.example.administrator.okhttpdemo;

import android.util.Log;

/**
 * Created by Administrator on 2017/3/1 0001.
 */

public class L {
    public static Boolean debug = true;
    public static void e(String content){
         if(debug){
             Log.e("OkHttpDemo", content);
                    }
    }
}
