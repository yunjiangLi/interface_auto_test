package com.test.day01;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class GetResponse{

    @Test
    public void firstGetResponse(){
        given().

        when().
                get("http://www.httpbin.org/get").
        then().
                log().all();
    }
//传递参数
    @Test
    public void getDemo01(){
        given().
                queryParam("wd","柠檬班").
                queryParam("ie","utf-8").
        when().
                get("https://www.baidu.com").
        then().
                log().all();
    }
}
