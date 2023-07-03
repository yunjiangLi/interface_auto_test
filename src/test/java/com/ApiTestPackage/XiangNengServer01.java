//注册、登录
package com.ApiTestPackage;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class XiangNengServer01 {
    //短信获取验证码接口
    String phone = "18700010003";
    String userName = "娃娃03";
    String pwd = "1234567abc";
    String url = "http://47.115.15.198:7001/smarthome";

    @Test
    public String verification(){
        Response res =
        given().
                queryParam("phone",phone).
        when().
                get(url + "/verificationCode/message").
        then().
                log().all().extract().response();
        //System.out.println((String) res.jsonPath().get("data"));
        return res.jsonPath().get("data");
    }
//注册接口
    @Test
    public void register(){
        String jsonData = "{\n" +
                "  \"phone\": \"" + phone + "\",\n" +
                "  \"pwd\": \"" + pwd +"\",\n" +
                "  \"rePwd\": \"" + pwd +"\",\n" +
                "  \"userName\": \"" + userName +"\",\n" +
                "  \"verificationCode\": \"" + verification() + "\"\n" +
                "}";
        Response res =
        given().
                body(jsonData).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                post(url +"/user/register").
                then().
                log().all().extract().response();
        System.out.println(res.jsonPath());
    }

    //登录
    @Test
    public void login(){
        String loginData = "{\n" +
                "  \"pwd\": \""+ pwd + "\",\n" +
                "  \"userName\": \""+ userName +"\"\n" +
                "}";
        given().
                body(loginData).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                post(url + "/user/login").
                then().
                log().all();
    }
}
