package com.mk.slidedelete;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by cabbageMk on 2016/9/4.
 */
public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        }
        toast.setText(msg);
        toast.show();
    }
}
