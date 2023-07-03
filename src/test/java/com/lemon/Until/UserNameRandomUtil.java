package com.lemon.Until;

import org.apache.commons.lang3.RandomStringUtils;

public class UserNameRandomUtil {
    public static String getUsername(){
        RandomStringUtils randomStringUtils = new RandomStringUtils();
        String name = null;
        name = randomStringUtils.randomAlphanumeric(4);
        return name;
    }

    public static void main(String[] args) {
        System.out.println(getUsername());
    }
}
