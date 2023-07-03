package com.test.day01;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

public class PostResponse {
    @Test
    public void firstPostResponse() {
        given().
                header("contentType", "application/json").
                when().
                post("http://www.httpbin.org/post").
                then().log().all();
    }
//参数为form表单类型
    @Test
    public void formPostDemo() {
        given().
                formParam("phone", "18700010002").
                formParam("name", "zhangsan").
                when().
                post("http://www.httpbin.org/post").
                then().log().body();
    }
    //参数为json类型
    @Test
    public void jsonPostDemo(){
        String jsonData = "{\"pwd\": \"1234567abc\",\"userName\": \"娃娃01\"}";
        given().
            body(jsonData).contentType(ContentType.JSON).
        when().
                post("http://www.httpbin.org/post").
        then().log().body();
    }
    //参数为Xml类型
    @Test
    public void xmlPostDemo(){
        String xmlData = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<suite>\n" +
                        "  <class>测试xml</class>\n" +
                        "</suite>";;
        given().
                body(xmlData).contentType(ContentType.XML).
                when().
                post("http://www.httpbin.org/post").
                then().log().body();
    }
    @Test
    public void filePostDemo(){
        given().
                multiPart(new File("E:\\测试用例\\前程贷业务用例.xlsx")).
                when().
                    post("http://www.httpbin.org/post").
                then().log().body();

    }}