package com.moin.doctors.Helper;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by macpro on 9/15/16.
 */
public class VolleySingelton {
    public static VolleySingelton my_Volley_Singelton_Reference;
    private RequestQueue my_requestQueue;

    private VolleySingelton(){
        my_requestQueue= Volley.newRequestQueue(MyApplication.getAppContext());
    }

    public static VolleySingelton getMy_Volley_Singelton_Reference(){
        if(my_Volley_Singelton_Reference == null){
            my_Volley_Singelton_Reference = new VolleySingelton();
        }
        return my_Volley_Singelton_Reference;
    }

    public RequestQueue getRequestQueue(){
        return my_requestQueue;
    }
}
