package org.blackey.utils;

import android.content.Context;

import com.vondear.rxtool.RxTool;

import java.io.InputStream;
import java.util.Properties;

public class ProperTies {

    public static Properties getProperties(Context c){
        Properties urlProps;
        Properties props = new Properties();
        try {
            //方法一：通过activity中的context攻取setting.properties的FileInputStream
            //注意这地方的参数appConfig在eclipse中应该是appConfig.properties才对,但在studio中不用写后缀
            //InputStream in = c.getAssets().open("appConfig.properties");
            InputStream in = c.getAssets().open("AppConfig");
            //方法二：通过class获取setting.properties的FileInputStream
            //InputStream in = PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
            props.load(in);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        urlProps = props;
        return urlProps;
    }


    public static String getProperty(String key){

        Properties proper = ProperTies.getProperties(RxTool.getContext());
        String serviceUrl = proper.getProperty(key);
        return serviceUrl;
    }


}
