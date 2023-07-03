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

public class CompleteTest extends BaseTest {
    @BeforeClass
    public void setUp(){
        List<ExcelPojo> listDatas = readSpecifyTestCases(4,1,3);
        //获取验证码
        Response resData1 = request(listDatas.get(0),"verificationCode");
        extractToEnv(listDatas.get(0),resData1);
        //正则替换注册接口请求参数
        caseReplace(listDatas.get(1));
        //注册
        Response resData2 = request(listDatas.get(1),"register");
        extractToEnv(listDatas.get(1),resData2);
        //登录
        caseReplace(listDatas.get(2));
        Response res = request(listDatas.get(2),"login");
        extractToEnv(listDatas.get(2),res);
    }
    @Test(dataProvider = "getCompleteData")
    public void complete(ExcelPojo excelPojo){
        caseReplace(excelPojo);
        Response resComplete = request(excelPojo,"complete");
        assertResponse(excelPojo,resComplete);
    }
    @DataProvider
    public Object[] getCompleteData(){
        List<ExcelPojo> completeData = readSpecifyTestCases(4,4);
        return completeData.toArray();
    }
    @AfterClass
    public void teardom(){
        Enviroment.envMap.clear();
    }
}
