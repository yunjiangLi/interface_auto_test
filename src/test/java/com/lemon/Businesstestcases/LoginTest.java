package com.lemon.Businesstestcases;

import com.lemon.common.BaseTest;
import com.lemon.data.Enviroment;
import com.lemon.pojo.ExcelPojo;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class LoginTest extends BaseTest {
    @BeforeClass
    public void setUp(){
        List<ExcelPojo> listDatas = readSpecifyTestCases(3,1,2);
/*
        //正则替换用例中电话号
        regexReplace(listDatas.get(0));
*/
        //短信验证码获取接口
        Response resData1 = request(listDatas.get(0),"verificationCode");
        extractToEnv(listDatas.get(0),resData1);
        //正则替换注册接口请求参数
        caseReplace(listDatas.get(1));
        Response resData2 = request(listDatas.get(1),"register");
        //extractToEnv(listDatas.get(1),resData2);
    }
    @Test(dataProvider = "getLoginDatas")
    public void loginTestCase(ExcelPojo excelPojo){
        //正则替换login接口的请求数据和预期响应结果
        caseReplace(excelPojo);
        //返回login接口的响应数据
        Response resLogin = request(excelPojo,"login");
        assertResponse(excelPojo,resLogin);
       // assertSQL(excelPojo);
    }
    @DataProvider
    public Object[] getLoginDatas(){
        List<ExcelPojo> listData = readSpecifyTestCases(3,3);
        return listData.toArray();//toArray方法将list类型转化为数组
    }
    @AfterClass
    public void teardom(){
        Enviroment.envMap.clear();
    }
  }
