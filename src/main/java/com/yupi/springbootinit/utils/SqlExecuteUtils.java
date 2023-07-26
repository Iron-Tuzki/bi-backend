package com.yupi.springbootinit.utils;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author lanshu
 * @date 2023-07-25
 */
@Component
public class SqlExecuteUtils {


    @Resource
    private SqlSessionFactory sqlSessionFactory;

    public void execute(String sql) throws SQLException {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection connection = sqlSession.getConnection();
        try (Statement statement = connection.createStatement()) {
            boolean execute = statement.execute(sql);
        }
        sqlSession.commit();
    }
}
