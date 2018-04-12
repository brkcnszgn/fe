package com.example.burakcan.fe.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.example.burakcan.fe.Model.User;

public class Common {
    public static User currentUser;
    public static String convertCodeToStatus(String status) {
        switch (status) {
            case "0":
                return "Siparis Alindi";
            case "1":
                return "Yolda";
            default:
                return "Teslim Edildi";
        }
    }



    public static final String DELETE  = "Sil";
    public static final String USER_KEY  = "User";
    public static final String PWD_KEY  = "Password"; //BENI HATIRLA

}
