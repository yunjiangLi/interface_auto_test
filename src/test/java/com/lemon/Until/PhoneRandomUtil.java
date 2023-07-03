package com.lemon.Until;

import com.lemon.data.Enviroment;

import java.util.Random;

public class PhoneRandomUtil {
    //随机生成电话号码
    public static String getRandomPhone(){
        Random random = new Random();
        String phonePrefix = "133";
        int num;
        for ( int i = 0; i < 8; i++ ) {
            num = random.nextInt(9);
            phonePrefix = phonePrefix + num;
        }
        return phonePrefix;
    }
    //数据库中查询是否存在该电话号码，如果存在则重新生成，如果不存在则返回该号码
    public static String getUnregisterPhone(){
        String phone = null;
        while(true){
            phone = getRandomPhone();
            Object result = JDBCUtils.SingleData("select count(*) FROM member WHERE mobile_phone = "+ phone);
            if((Long)result == 0){
                break;
            }
        }
        return phone;
    }
    //电话号码存入参数池
    public void putEnviroment(){
        //随机生成电话号码，并查询数据库中是否存在
        String phone = PhoneRandomUtil.getUnregisterPhone();
        //将电话号码写入参数池（环境变量）中
        Enviroment.envMap.put("phone",phone);
    }


}
