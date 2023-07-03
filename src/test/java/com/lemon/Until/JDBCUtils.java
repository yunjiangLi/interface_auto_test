package com.lemon.Until;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.lemon.data.Constants.*;

public class JDBCUtils {

    public static Connection getConnections(){
        //每个项目的url定义有所区别
        String url = "jbdc:mysql://"+DB_BASE_URI+"/"+DB_NAME+"?useUnicode=true&characterEncoding=utf-8";
        String user = DB_USER;
        String password = DB_PWD;
        //定义数据库链接对象
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url,user,password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return  connection;
    }
    //更新类sql语句：包含增、删、改
    public static void update(String sql){
        Connection connection = getConnections();
        //QueryRunner是Apache组织提供的一个开源 JDBC工具类库,QueryRunner中提供对sql语句操作的API
        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.update(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    //查询类sql语句
    // 1.  输出结果为一个数据
    public static Object SingleData(String sql){
        Connection connection = getConnections();
        QueryRunner queryRunner = new QueryRunner();
        Object result = null;
        try {
            result = queryRunner.query(connection,sql,new ScalarHandler<>());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeConnection(connection);
        }
        return result;
    }
    // 2.  输出结果为一条列表数据
    public static Map<String, Object> OneData(String sql){
        Connection connection = getConnections();
        QueryRunner queryRunner = new QueryRunner();
        Map<String, Object> result = null;
        try {
            result = queryRunner.query(connection,sql,new MapHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeConnection(connection);
        }

        return result;
    }
    // 3.  输出结果为多条列表数据
    public static List<Map<String,Object>> ListDatas(String sql){
        Connection connection = getConnections();
        QueryRunner queryRunner = new QueryRunner();
        List<Map<String, Object>> result = null;
        try {
            result = queryRunner.query(connection,sql,new MapListHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            closeConnection(connection);
        }
        return result;
    }
//关闭数据库连接
    public static void closeConnection(Connection connection){
        try {
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
