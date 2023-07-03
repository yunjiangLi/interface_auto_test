package com.lemon.common;
//该类中定义所有参数方法中要调用的基本方法

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.lemon.Until.JDBCUtils;
import com.lemon.data.Constants;
import com.lemon.data.Enviroment;
import com.lemon.pojo.ExcelPojo;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.LogConfig;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class BaseTest {
    @BeforeTest
    public void GlobalSetup() throws FileNotFoundException {
        //返回json为Decimal数据类型
        RestAssured.config = RestAssured.config().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        //BaseUrl全局配置
        RestAssured.baseURI= Constants.BASE_URI;
    }
    //restAssured框架封装
    public Response request(ExcelPojo excelPojo,String interfaceModuleName){
        String logFilepath;
        //Constants.LOG_TO_FILE值为false,日志信息输出到控制台，为ture输出到allure报表中
        if(Constants.LOG_TO_FILE){
            File dirPath = new File(System.getProperty("user.dir")+"\\log\\"+interfaceModuleName);
            if(!dirPath.exists()){
                dirPath.mkdirs();
            }
            logFilepath = dirPath +"\\test"+ excelPojo.getCaseId() + ".log";
            PrintStream fileOutPutStream = null;
            try {
                fileOutPutStream =new PrintStream(new File(logFilepath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAssured.config =RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));

        }
        String url = excelPojo.getUrl();
        String heards = excelPojo.getRequestHeader();
        String params = excelPojo.getInputParams();
        String method = excelPojo.getMethod();
        //将heard字符串转化为map
        Map<String,Object> heardsMap = JSONObject.parseObject(heards,Map.class);
        Response res = null;
        //根据接口类型判断restAssured框架
        if(method.equalsIgnoreCase("get")){
            res = given().headers(heardsMap).when().get(url).then().log().all().extract().response();
        }else if(method.equalsIgnoreCase("post")){
            res = given().body(params).headers(heardsMap).when().post(url).then().log().all().extract().response();
        }else if(method.equalsIgnoreCase("put")){
            res = given().body(params).headers(heardsMap).when().put(url).then().log().all().extract().response();
        }
        //向Allure报表中添加日志
        if(Constants.LOG_TO_FILE) {
            try {
                Allure.addAttachment("接口响应数据",new FileInputStream(logFilepath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return res;
    }
    //读取excel中指定sheet页的指定行用例---easyPOI
    public List<ExcelPojo> readAllTestCases(int sheetNum, int startRowNum, int readRows){
        File file = new File(Constants.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1);
        importParams.setStartRows(startRowNum);
        importParams.setReadRows(readRows);
        List<ExcelPojo> list =ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
        return list;
    }
    //读取excel中指定sheet页的指定行用例
    public List<ExcelPojo> readSpecifyTestCases(int sheetNum, int startRowNum, int readRows){
        File file = new File(Constants.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1);
        importParams.setStartRows(startRowNum-1);
        importParams.setReadRows(readRows);
        List<ExcelPojo> list = ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
        return list;
    }
    //读取excel中指定sheet页的指定行开始到结束的所有用例
    public List<ExcelPojo> readSpecifyTestCases( int sheetNum, int startRowNum){
        File file = new File(Constants.EXCEL_FILE_PATH);
        ImportParams importParams = new ImportParams();
        importParams.setStartSheetIndex(sheetNum-1);
        importParams.setStartRows(startRowNum-1);
        List<ExcelPojo> list = ExcelImportUtil.importExcel(file,ExcelPojo.class,importParams);
        return list;
    }
    //将关联的参数存储到环境变量中
    public void extractToEnv(ExcelPojo excelPojo,Response res){
        Map<String,Object> extractMap = JSON.parseObject(excelPojo.getExtract());
        //使用for循环遍历extract中的所有k-v对
        for ( String key:extractMap.keySet()) {
            //获取gPath路径
            Object path = extractMap.get(key);
            //获取GPath对应的值
            Object value = res.jsonPath().get(path.toString());
            //将值存储到环境变量中
            Enviroment.envMap.put(key,value);
        }
    }

    //正则替换
    public static String regexPlace(String orgStr) {
        //判断要被正则替换的数据是否为空，为空时则不进行替换，为防止抛出空指针异常
        if(StringUtils.isEmpty(orgStr)) {
            return orgStr;
        }
        //正则替换-定义正则表达式
        Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
        //定义匹配对象
        Matcher matcher = pattern.matcher(orgStr);
        String result = orgStr;
        //获取匹配对象并进行替换
        while (matcher.find()) {
            //group(0)表示获取连同{{}}在内的整个匹配对象
            String outerStr = matcher.group(0);
            //group(10)表示获取不带{{}}的匹配对象
            String innerStr = matcher.group(1);
            Object replaceStr = Enviroment.envMap.get(innerStr);
            result = result.replace(outerStr, replaceStr.toString());
        }
        return result;
    }
    //正则替换方法封装，包含：请求参数、url、请求头、期望返回结果中的某些字段值需要进行正则替换
    public ExcelPojo caseReplace(ExcelPojo excelPojo){
        //请求参数正则替换
        String inputParams = regexPlace(excelPojo.getInputParams());
        //使用set方法将修改后的数据写回
        excelPojo.setInputParams(inputParams);
        //请求头正则替换
        String requestHeard = regexPlace(excelPojo.getRequestHeader());
        excelPojo.setRequestHeader(requestHeard);
        //url正则替换
        String url = regexPlace(excelPojo.getUrl());
        excelPojo.setUrl(url);
        //预期结果正则替换
        String expected = regexPlace(excelPojo.getExpected());
        excelPojo.setExpected(expected);
        //数据库校验正则替换
        String dbAssert = regexPlace(excelPojo.getDbAssert());
        excelPojo.setExpected(dbAssert);

        return excelPojo;
    }
    //响应数据断言
    public void assertResponse(ExcelPojo excelPojo ,Response res){
        String expected = excelPojo.getExpected();
        if(expected != null) {
            //取接口的预期响应结果，并将其转化为map类型
            Map<String, Object> expectedMap = (Map) JSON.parse(excelPojo.getExpected());
            //断言
            for ( String key : expectedMap.keySet() ) {
                //String actual = key.toString();
                //System.out.println(key);
                //System.out.println(expectedMap.get(key));
                Assert.assertEquals(res.jsonPath().get(key), expectedMap.get(key));
            }
        }
    }
    //数据库断言
    public void assertSQL(ExcelPojo excelPojo){
        //提取excel中数据库校验字段数据
        String dbAssert = excelPojo.getDbAssert();
        if(dbAssert != null) {
            //将提取出来的json数据转化为map类型
            Map<String, Object> map = JSONObject.parseObject(dbAssert, Map.class);
            //获取map的所以键及就是sql语句
            Set<String> keys = map.keySet();
            //对键循环
            for (String key : keys) {
                //key其实就是我们执行的sql语句
                //value就是数据库断言的期望值
                Object expectedValue = map.get(key);
                //System.out.println("expectedValue类型::" + expectedValue.getClass());
                //判断期望值的类型
                if(expectedValue instanceof BigDecimal){
                    Object actualValue = JDBCUtils.SingleData(key);
                    //System.out.println("actualValue类型:" + actualValue.getClass());
                    Assert.assertEquals(actualValue,expectedValue);
                }
                else if(expectedValue instanceof Integer){
                    //此时从excel里面读取到的是integer类型
                    //从数据库里面拿到的是Long类型
                    Long expectedValue2 = ((Integer) expectedValue).longValue();
                    Object actualValue = JDBCUtils.SingleData(key);
                    Assert.assertEquals(actualValue,expectedValue2);
                }
            }
        }
    }

}
