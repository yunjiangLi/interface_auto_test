package com.test.day01;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class GetResValue {
    @Test
    public void getJsonValue(){
        String jsonData = "{\"pwd\": \"1234567abc\",\"userName\": \"娃娃01\"}";
        Response res =
        given().
                body(jsonData).contentType(ContentType.JSON).
        when().
                post("http://www.httpbin.org/post").
        then().
                log().all().extract().response();
        //获取接口响应时间
        System.out.println(res.time());
        //获取接口响应头某字段值
        System.out.println(res.header("Content-Type"));
        //获取接口响应体某字段值
        System.out.println((String) res.jsonPath().get("json.pwd"));
    }

}
