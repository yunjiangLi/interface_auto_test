package com.Singletestcases;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.lemon.pojo.ExcelPojo;
import com.alibaba.fastjson2.JSON;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class LogintestCases {
    @Test (dataProvider = "getLoginDatas")
    public void loginTestCase(ExcelPojo excelPojo){
        RestAssured.config = RestAssured.config().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        RestAssured.baseURI = "http://47.115.15.198:7001/smarthome";
        Map<String,Object> heardMap = (Map) JSON.parse(excelPojo.getRequestHeader());
        given().
                body(excelPojo.getInputParams()).headers(heardMap).
                when().
                post(excelPojo.getUrl()).
                then().
                log().body().extract().response();
        //System.out.println((String) res.jsonPath().get("msg"));
    }
    @DataProvider
    public Object[] getLoginDatas(){
        File file = new File("C:\\Users\\Administrator\\Desktop\\自动化课程\\项目文档\\湘能物业项目接口测试用例.xlsx");
        List<ExcelPojo> listData = readSpecifyTestCases(file,3,3,3);
        return listData.toArray();//toArray方法将list类型转化为数组
    }
    //读取excel中指定sheet页的指定行用例
    public static List<ExcelPojo> readSpecifyTestCases(File file, int sheetNum, int startRowNum, int readRows){
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1);
        importParams.setStartRows(startRowNum-1);
        importParams.setReadRows(readRows);
        List<ExcelPojo> list = ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
        return list;
    }

//    public static void main(String[] args) {
//        File file = new File("C:\\Users\\Administrator\\Desktop\\自动化课程\\项目文档\\湘能物业项目接口测试用例.xlsx");
//        List<ExcelPojo> listData = readSpecifyTestCases(file,3,3,3);
//        System.out.println(listData);
//    }
}
