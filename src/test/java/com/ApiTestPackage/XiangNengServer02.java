//商业、物业信息添加或完善
package com.ApiTestPackage;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class XiangNengServer02 {
    static String url = "http://47.115.15.198:7001/smarthome";
    static int agentId = 8921;
    static int serverId = 7921;
    int agentTypeCode = 2;
    int serverIdTypeCode = 3;
    String agentPel = "15900010002";
    String serverPel = "17700010002";
    //添加代理商信息
    @Test
    public void merchartAgent(){
        String agentInfor = "{\n" +
                "  \"address\": \"陕西省西安市高新区x001街道\",\n" +
                "  \"establishDate\": \"2023-05-28\",\n" +
                "  \"legalPerson\": \"东仔\",\n" +
                "  \"licenseCode\": \"essert202305280112\",\n" +
                "  \"licenseUrl\": \"http://127.0.0.1/smarthome/testinghha.jpg\",\n" +
                "  \"merchantName\": \"浙江武文代理有限公司\",\n" +
                "  \"merchantType\": " + agentTypeCode + ",\n" +
                "  \"registerAuthority\": \"西咸新区派出所\",\n" +
                "  \"tel\": \""+ agentPel +"\",\n" +
                "  \"userId\": "+agentId+",\n" +
                "  \"validityDate\": \"2038-05-06\"\n" +
                "}";
        given().
                body(agentInfor).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                put(url + "/merchant/complete").
                then().
                log().body();
    }
    //添加信物业息
    @Test
    public void merchartServer(){
        String serverInfor = "{\n" +
                "  \"address\": \"陕西省西安市高新区x001街道\",\n" +
                "  \"establishDate\": \"2021-09-26\",\n" +
                "  \"legalPerson\": \"张三\",\n" +
                "  \"licenseCode\": \"aste202109260089\",\n" +
                "  \"licenseUrl\": \"http://127.0.0.1/smarthome/testinghyy.jpg\",\n" +
                "  \"merchantName\": \"诚挚物业有限公司\",\n" +
                "  \"merchantType\": "+ serverIdTypeCode +",\n" +
                "  \"registerAuthority\": \"西咸新区派出所\",\n" +
                "  \"tel\": \""+ serverPel +"\",\n" +
                "  \"userId\": " + serverId + ",\n" +
                "  \"validityDate\": \"2031-09-27\"\n" +
                "}";
        given().
                body(serverInfor).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                put(url + "/merchant/complete").
                then().
                log().body();
    }

}
