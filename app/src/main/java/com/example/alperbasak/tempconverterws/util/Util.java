package com.example.alperbasak.tempconverterws.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.EditText;
import android.widget.Toast;

public final class Util {

    private static  Util INSTANCE;
    private static Context context;

    private Util(Context context) {
        Util.context =context;
    }

    public static Util getInstance(Context context) {

        if (INSTANCE == null) {
            INSTANCE=new Util(context);
        }
            return INSTANCE;
    }


    public void showMessage(String message){
        Toast.makeText(context, message,Toast.LENGTH_SHORT).show();
    }

    public boolean isInternetActive(){
        ConnectivityManager connectivityManager= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
       return networkInfo!=null && networkInfo.isConnected();
    }

    public static boolean isTextEmpty(EditText editText){
        return editText.getText().toString().trim().isEmpty();
    }
}

