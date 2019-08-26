package com.octane.app;

import android.text.TextUtils;
import android.util.Patterns;

public class GlobalConstant {
    public final  static int PACKET_MIN_SIZE = 10;
    public final  static int REQUEST_CODE = 100;
    public final static String SPECIFIC_BLUETOOTH_DEVICE = "RAC3RCHIP";
    public final static float ONE_EFFECT_TIME = 0.05f; //2ms
    public final static String PROFILE_DESC = "Puts your vehicle into ";
    //public final static int ANTITHEFT_INDEX = 4;
    public final static int RED_COLOR_INDEX = 3;
    public final static String MSG_ONE_CLICK = "Please double click to navigate!";



    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
