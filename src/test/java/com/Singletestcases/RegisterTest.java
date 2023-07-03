/*
* 单接口测试---前置用例：获取短信验证码，测试用例：注册用例
* time：2023.6.6
* writer：李昀
* */
package com.Singletestcases;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson2.JSON;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;



//注册的前置用例为verificationCode 获取验证码用例
//前置用例必须为正常用例，且无需进行断言
public class RegisterTest {
    static String verificationCode;
    @BeforeTest
    public void setUp(){
        RestAssured.config = RestAssured.config().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        RestAssured.baseURI = "http://47.115.15.198:7001/smarthome";

        File file = new File("C:\\Users\\Administrator\\Desktop\\自动化课程\\项目文档\\湘能物业项目接口测试用例.xlsx");
        List<com.Singletestcases.ExcelPojo> registerData = readSpecifyTestCases(file,1,1,1);
    Response resData =
        given().
                body(registerData.get(0).getInputParams()).contentType(ContentType.JSON).
        when().
                get(registerData.get(0).getUrl()).
        then().
                log().body().extract().response();
    verificationCode = resData.jsonPath().get("data");

    }
    @Test (dataProvider = "getRegisterDatas")
    public void register(ExcelPojo excelPojo){
        String inputParams = excelPojo.getInputParams();
        String requestHeards = excelPojo.getRequestHeader();
        String url = excelPojo.getUrl();
        String method = excelPojo.getMethod();
        String expected = excelPojo.getExpected();
        Map requestHeardsMap = (Map) JSON.parse(requestHeards);
        Map<String ,Object> expectedMap = (Map) JSON.parse(expected);
        Response res =
                given().
                        body(inputParams).contentType(ContentType.JSON).
                        headers(requestHeardsMap).
                        when().
                        post(url).
                        then().
                        log().all().extract().response();
        for(String key : expectedMap.keySet()) {
            //String actual = key.toString();
            //System.out.println(key);
            //System.out.println(expectedMap.get(key));
            Assert.assertEquals(res.jsonPath().get(key),expectedMap.get(key));
        }
    }
    @DataProvider
    public Object[] getRegisterDatas(){
        //int sheetNum = 2;
        File file = new File("C:\\Users\\Administrator\\Desktop\\自动化课程\\项目文档\\湘能物业项目接口测试用例.xlsx");
        List<ExcelPojo> listDatas = readSpecifyTestCases(file, 1,2,17);
        return listDatas.toArray();//toArray方法将list类型转化为数组
    }
    //读取excel指定sheet页中所有用例
    public List<ExcelPojo> readAllTestCases(File file, int sheetNum){
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1);
        List<ExcelPojo> list = ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
//       for(Object object:listDatas){
//           System.out.println(object);
//       }
        return list;
    }

    //读取excel中指定sheet页的指定行用例
    public List<ExcelPojo> readSpecifyTestCases(File file, int sheetNum, int startRowNum, int readRows){
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1);
        importParams.setStartRows(startRowNum-1);
        importParams.setReadRows(readRows);
        List<com.Singletestcases.ExcelPojo> list =ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
        return list;
    }



}
