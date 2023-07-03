//添加小区及审核,物业入驻小区及物业信息审核,小区添加楼栋，单元，房号
package com.ApiTestPackage;

import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class XiangNengServer03 extends XiangNengServer02 {
     int communityId = communityAdd();
    //static int serverId = SerInCom();
    int page = 2;
    int size = 10;
    int buildingId;
    int unitId;
    int roomId;
    // 添加社区
    public int communityAdd(){
        RestAssured.config = RestAssured.config().jsonConfig(JsonConfig.jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
        RestAssured.baseURI  = "http://47.115.15.198:7001/smarthome";
        String add = "{\n" +
                "  \"address\": \"青海省西宁市城东区格兰小镇\",\n" +
                "  \"communityName\": \"八家湾小区\",\n" +
                "  \"nearbyLandmarks\": \"王府井\",\n" +
                "  \"userId\": "+ agentId +"\n" +
                "}";

        Response res =
        given().
                body(add).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                post("/community/add").
                then().log().all().extract().response();
        return res.jsonPath().get("data.communityId");
    }

    // 社区审核
    @BeforeSuite
    public void examineCommunity(){
        String examineData = "{\n" +
                "  \"communityId\": " + communityId +",\n" +
                "  \"remark\": \"通过\",\n" +
                "  \"state\": 1\n" +
                "}";
        given().
                body(examineData).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                post("/community/examine").
                then().log().body();
        System.out.println("社区审核通过");
    }
    //物业入驻小区
    public int serInCom(){
        Response res =
        given().
                queryParam("communityId",communityId).
                queryParam("userId",serverId).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                post("/community/in").
                then().log().body().extract().response();
        return res.jsonPath().get("data.commuMemberId");
    }

    // 物业信息审核
   @BeforeSuite
    public void testExamineServer(){
        String examineData = " {\n" +
                "  \"commuMemberId\": "+ serInCom() +",\n" +
                "  \"remark\": \"通过或未通过的原因\",\n" +
                "  \"state\": 1\n" +
                "}";
        Response res = given().
                body(examineData).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                post("/community/in/examine").
                then().log().body().extract().response();
       System.out.println("物业审核通过，入驻小区成功");
    }
    // 添加楼栋
    @Test(groups = "CommunInfor")
    public void buildAdd(){
        String buildData = "{\n" +
                "  \"buildingName\": \"1号楼\",\n" +
                "  \"buildingNum\": \"001\",\n" +
                "  \"communityId\": " + communityId+ ",\n" +
                "  \"floorage\": 300,\n" +
                "  \"remark\": \"这是第一栋楼\",\n" +
                "  \"userId\": "+serverId+"\n" +
                "}";
        Response res =
        given().
                body(buildData).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                post("/building/add").
                then().log().body().extract().response();
        buildingId = res.jsonPath().get("data.buildingId");
        System.out.println("添加楼栋成功,楼栋id = "+buildingId);
    }
    //楼栋数据查询
    @Test
    public void buildList(){
        given().header("X-Lemonban-Media-Type","lemonban.v1").
                when().get("/building/"+page+"/"+ size+"/list").
                then().log().body();
    }
    //楼栋添加单元
    @Test(dependsOnMethods = "buildAdd",groups = "CommunInfor")
    public void unitAdd(){
        String unitData =  "{\n" +
                "    \"buildingId\": "+ buildingId +",\n" +
                "    \"floorage\": 340,\n" +
                "    \"layerCount\": 32,\n" +
                "    \"lift\": 1,\n" +
                "    \"remark\": \"这是一个备注\",\n" +
                "    \"unitNum\": \"001\",\n" +
                "    \"userId\": "+ serverId +"\n" +
                "}";
        Response resUnit =
        given().
                body(unitData).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                post("/unit/add").then().log().body().extract().response();
        unitId = resUnit.jsonPath().get("data.unitId");
        System.out.println("添加单元成功，单元id = "+ unitId);
    }
//单元添加房号
    @Test(dependsOnGroups="CommunInfor")
    public void roomAdd(){
        String roomInfor = "{\n" +
                "  \"builtUpArea\": 120,\n" +
                "  \"communityId\": " + communityId + ",\n" +
                "  \"layer\": 6,\n" +
                "  \"remark\": \"这是一个备注\",\n" +
                "  \"roomNum\": \"603\",\n" +
                "  \"roomStyle\": \"三室两厅\",\n" +
                "  \"unitId\": "+unitId+",\n" +
                "  \"userId\": "+serverId+"\n" +
                "}";
        Response roomRes =
        given().
                body(roomInfor).contentType(ContentType.JSON).
                header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                post("/room/add").
                then().log().body().extract().response();
        roomId = roomRes.jsonPath().get("data.roomId");
        System.out.println("房间信息添加成功，房号id = "+ roomId);
       String actualRoomNum = roomRes.jsonPath().get("data.roomNum");
       Assert.assertEquals(actualRoomNum,"603");
    }

    //业主相关操作
    //添加业主
//    public void ownerAdd(){
//
//    }

}
