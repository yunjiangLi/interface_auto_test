package com.DriverDatas;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson2.JSON;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class DriverData {
    @Test(dataProvider = "getLoginDatas")
    public void login(ExcelPojo excelPojo){
        RestAssured.config = RestAssured.config().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        RestAssured.baseURI = "http://47.115.15.198:7001/smarthome";
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

//    @DataProvider
//    public Object[][] getLoginDatas(){
//        Object[][] datas = {{"1234567abc","娃娃03"},{"1234567abb","娃娃03"},{"1234567abc","娃娃"}};
//        return datas;
//    }
    @DataProvider
    public Object[] getLoginDatas(){
        File file = new File("C:\\Users\\Administrator\\Desktop\\自动化课程\\项目文档\\湘能物业项目接口测试用例.xlsx");
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(1);
        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
//       for(Object object:listDatas){
//           System.out.println(object);
//       }
        return listDatas.toArray();//toArray方法将list类型转化为数组
        //return new Object[0];
    }
}
