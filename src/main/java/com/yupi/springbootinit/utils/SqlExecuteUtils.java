package com.yupi.springbootinit.utils;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * @author lanshu
 * @date 2023-07-25
 */
@Component
public class SqlExecuteUtils {



    @Resource
    private SqlSessionFactory sqlSessionFactory;

    public void execute(String sql) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            Connection connection = sqlSession.getConnection();
            try (Statement statement = connection.createStatement()){
                ResultSet resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    // 获取字段值
                    String userName= resultSet.getString("userName");
                    String userPassword = resultSet.getString("userPassword");
                    System.out.println("*****************************");
                    System.out.println(userName);
                    System.out.println(userPassword);
                }
            }
            sqlSession.commit();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
