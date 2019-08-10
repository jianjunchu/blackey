package org.blackey.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import org.blackey.R;

/**
 * 获取地区号码工具类
 * <p/>
 * created by OuyangPeng on 2017/1/6.
 */
public class ResourcesUtils {
    private static final String TAG = "ResourcesUtils";
    // 区号前缀 加号
    public static final String AREA_CODE_PLUS = "+";

    /**
     * 根据国家Code返回国家Name
     *
     * @param countryCode
     * @return
     */
    public static String getCountryNameByCountryCode(Context context, String countryCode) {
        //获得对于的Key值  在R文件中的id
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(countryCode.toUpperCase(), "string", context.getApplicationContext().getPackageName());
        //根据id来获得key对应的value值
        if (0 == resourceId) {
            Log.e(TAG,"countryCode " + countryCode + " is NULL!");
            return "";
        } else {
            return resources.getString(resourceId);
        }
    }

    /**
     * 根据国家Code返回国家Name
     *
     * @return
     */
    public static int getPaymentModeIcon(Context context, String name) {
        //获得对于的Key值  在R文件中的id
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(name, "mipmap", context.getApplicationContext().getPackageName());
        //根据id来获得key对应的value值
        if(resourceId == 0){
            return R.mipmap.payment_other;
        }else{
            return resourceId;
        }

    }

    public static String getPaymentName(Context context, String countryCode) {
        //获得对于的Key值  在R文件中的id
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(countryCode.toUpperCase(), "string", context.getApplicationContext().getPackageName());
        //根据id来获得key对应的value值
        if (0 == resourceId) {
            Log.e(TAG,"countryCode " + countryCode + " is NULL!");
            return "";
        } else {
            return resources.getString(resourceId);
        }
    }

    public static String getBankName(Application context, String bankSwiftCode) {
        //获得对于的Key值  在R文件中的id
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(bankSwiftCode.toUpperCase(), "string", context.getApplicationContext().getPackageName());
        //根据id来获得key对应的value值
        if (0 == resourceId) {
            Log.e(TAG,"countryCode " + bankSwiftCode + " is NULL!");
            return "";
        } else {
            return resources.getString(resourceId);
        }
    }
}
